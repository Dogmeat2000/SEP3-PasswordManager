package dk.sep3.dbserver.grpc.service;

import dk.sep3.dbserver.model.passwordManager.db_entities.User;
import dk.sep3.dbserver.grpc.adapters.grpc_to_java.MasterUserDTOtoMasterUserEntity;
import dk.sep3.dbserver.grpc.adapters.java_to_grpc.UserToGrpcUserData;
import dk.sep3.dbserver.service.passwordManager.UserRepositoryService;
import dk.sep3.dbserver.service.passwordManager.UserRepositoryServiceImpl;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

// TODO: DELETE THIS CLASS!

/** Responsible for handling incoming GRPC calls from external clients and returning results using gRPC. */
@GrpcService
public class UserGrpcServiceImpl extends UserServiceGrpc.UserServiceImplBase
{
  private final UserRepositoryService userServiceImpl;
  private static final Logger logger = LoggerFactory.getLogger(UserGrpcServiceImpl.class);

  @Autowired
  public UserGrpcServiceImpl(UserRepositoryServiceImpl userServiceImpl, ServerHealthMonitor serverHealthMonitor) {
    super();
    this.userServiceImpl = userServiceImpl;

    // Launch database health monitor on a separate thread:
    Thread healthMonitorThread = new Thread(() -> serverHealthMonitor.startService());
    healthMonitorThread.setDaemon(true);
    healthMonitorThread.start();
    logger.info("Server Health Monitoring Service is running!");
  }


  @Override
  public void registerUser(UserData request, StreamObserver<UserData> responseObserver) {
    try {
      // Translate received gRPC information from the client, into a Java/DB compatible
      // entity and attempt to register the User in the repository/database:
      User registeredUser =  userServiceImpl.registerUser(MasterUserDTOtoMasterUserEntity.convertToUserEntity(request));

      // If User registration fails:
      if (registeredUser == null)
        throw new Exception("Failed to register User");

      // Translate the registered user returned from the DB into a gRPC compatible type, before sending the User back to the client as confirmation:
      responseObserver.onNext(UserToGrpcUserData.convertToGrpcUserData(registeredUser));
      responseObserver.onCompleted();

    } catch (Exception e) {
      responseObserver.onError(Status.INTERNAL.withDescription("Error registering user.").withCause(e).asRuntimeException());
    }
  }


  @Override
  public void getUser(UserNameAndPswd request, StreamObserver<grpc.UserData> responseObserver){
    try {
      // Translate received gRPC information from the client, into Java compatible type:
      String username = request.getUsername();
      String encryptedPassword = request.getEncryptedPassword();

      // Attempt to read the User with the provided username and password:
      User user = userServiceImpl.fetchUser(username, encryptedPassword);

      // If User read failed:
      if (user == null)
        throw new NotFoundInDBException("User not found in database");

      // Translate the found User into gRPC compatible types, before transmitting back to client:
      responseObserver.onNext(UserToGrpcUserData.convertToGrpcUserData(user));
      responseObserver.onCompleted();
    } catch (NotFoundInDBException e) {
      responseObserver.onError(Status.NOT_FOUND.withDescription("User {" + request.getUsername() + "} not found in DB").withCause(e).asRuntimeException());
    } catch (Exception e) {
      responseObserver.onError(Status.INTERNAL.withDescription("Error fetching User").withCause(e).asRuntimeException());
    }
  }

}
