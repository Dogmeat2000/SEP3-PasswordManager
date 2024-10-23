package dk.sep3.dbserver.grpc.service;

import dk.sep3.dbserver.grpc.adapters.grpc_to_java.MasterUserDTOtoMasterUserEntity;
import dk.sep3.dbserver.grpc.factories.GenericResponseFactory;
import dk.sep3.dbserver.model.passwordManager.db_entities.User;
import dk.sep3.dbserver.service.passwordManager.UserRepositoryService;
import dk.sep3.dbserver.service.passwordManager.UserRepositoryServiceImpl;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.PasswordManagerServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class PasswordManagerGrpcServiceImpl extends PasswordManagerServiceGrpc.PasswordManagerServiceImplBase
{
  private final UserRepositoryService userServiceImpl;
  private static final Logger logger = LoggerFactory.getLogger(UserGrpcServiceImpl.class);

  @Autowired
  public PasswordManagerGrpcServiceImpl(UserRepositoryServiceImpl userServiceImpl, ServerHealthMonitor serverHealthMonitor) {
    super();
    this.userServiceImpl = userServiceImpl;

    // Launch database health monitor on a separate thread:
    Thread healthMonitorThread = new Thread(() -> serverHealthMonitor.startService());
    healthMonitorThread.setDaemon(true);
    healthMonitorThread.start();
    logger.info("Server Health Monitoring Service is running!");
  }

  @Override public void handleRequest(GenericRequest request, StreamObserver<GenericResponse> responseObserver){
    try {
      // Identify what actions was requested:
      String actionRequested = request.getRequestType().toLowerCase();

      // TODO: Refactor away from Switch, this is just the quick fix:
      GenericResponse response;
      User masterUser;
      switch(actionRequested) {
        case "readmasteruser":
          // Identify what type of DTO to convert to java compatible format:
          if(!request.getDataCase().equals(GenericRequest.DataCase.MASTERUSER))
            throw new Exception("Invalid request"); // TODO: Better exception handling.

          // Convert to db compatible entity:
          masterUser = MasterUserDTOtoMasterUserEntity.convertToUserEntity(request.getMasterUser());

          // Execute the proper action:
          masterUser = userServiceImpl.fetchUser(masterUser.getUsername(), masterUser.getEncryptedPassword());

          // Translate the response returned from the DB into a gRPC compatible type, before sending back to the client:
          response = GenericResponseFactory.buildGrpcGenericResponseWithMasterUserDTO(200, masterUser);

          responseObserver.onNext(response);
          responseObserver.onCompleted();

          break;

        case "createmasteruser":
          // Identify what type of DTO to convert to java compatible format:
          if(!request.getDataCase().equals(GenericRequest.DataCase.MASTERUSER))
            throw new Exception("Invalid request"); // TODO: Better exception handling.

          // Convert to db compatible entity:
          masterUser = MasterUserDTOtoMasterUserEntity.convertToUserEntity(request.getMasterUser());

          // Execute the proper action:
          masterUser = userServiceImpl.registerUser(masterUser);

          // Translate the response returned from the DB into a gRPC compatible type, before sending back to the client:
          response = GenericResponseFactory.buildGrpcGenericResponseWithMasterUserDTO(200, masterUser);

          responseObserver.onNext(response);
          responseObserver.onCompleted();

          break;

        default:
          response = GenericResponseFactory.buildGrpcGenericResponseWithError(500);
          responseObserver.onNext(response);
          responseObserver.onCompleted();
          break;

      }

    } catch (Exception e) {
      // Catch any exceptions: // TODO: Improve this with proper codes also!
      GenericResponse errorResponse = GenericResponseFactory.buildGrpcGenericResponseWithError(500);
      responseObserver.onNext(errorResponse);
      responseObserver.onCompleted();
    }
  }
}
