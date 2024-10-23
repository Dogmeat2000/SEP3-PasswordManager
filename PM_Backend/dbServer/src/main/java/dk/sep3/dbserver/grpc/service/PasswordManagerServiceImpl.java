package dk.sep3.dbserver.grpc.service;

import dk.sep3.dbserver.grpc.adapters.java_to_grpc.UserToGrpcUserData;
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
public class PasswordManagerServiceImpl extends PasswordManagerServiceGrpc.PasswordManagerServiceImplBase
{
  private final UserRepositoryService userServiceImpl;
  private static final Logger logger = LoggerFactory.getLogger(UserGrpcServiceImpl.class);

  @Autowired
  public PasswordManagerServiceImpl(UserRepositoryServiceImpl userServiceImpl, ServerHealthMonitor serverHealthMonitor) {
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
      String actionRequested = request.getRequestType();

      // TODO: Refactor away from Switch, this is just the quick fix:
      switch(actionRequested) {
        case "getuser":
          // Identify what type of DTO to convert to java compatible format:
          // TODO Missing implementation

          // Execute the proper action:
          // TODO Missing implementation

          // Translate the response returned from the DB into a gRPC compatible type, before sending back to the client:
          // TODO Missing implementation
          //responseObserver.onNext();
          //responseObserver.onCompleted();

          break;

        case "putuser":
          // Identify what type of DTO to convert to java compatible format:
          // TODO Missing implementation

          // Execute the proper action:
          // TODO Missing implementation

          // Translate the response returned from the DB into a gRPC compatible type, before sending back to the client:
          // TODO Missing implementation
          //responseObserver.onNext();
          //responseObserver.onCompleted();
          break;

        default:
          // TODO: Throw an exception
          break;

      }




    } catch (Exception e) {
      // Catch any exceptions:
      // TODO Missing implementation
    }
  }
}
