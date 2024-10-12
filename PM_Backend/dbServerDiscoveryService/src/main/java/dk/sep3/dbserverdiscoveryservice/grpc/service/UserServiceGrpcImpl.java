package dk.sep3.dbserverdiscoveryservice.grpc.service;

import dk.sep3.dbserver.model.discoveryService.db_entities.DatabaseServer;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryService;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryServiceImpl;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import dk.sep3.dbserverdiscoveryservice.service.DatabaseServerMonitor;
import grpc.UserData;
import grpc.UserNameAndPswd;
import grpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase
{
  private final DiscoveryRepositoryService discoveryRepositoryService;
  private final int numberOfRetries = 5;
  private static final Logger logger = LoggerFactory.getLogger(UserServiceGrpcImpl.class);

  @Autowired
  public UserServiceGrpcImpl(DiscoveryRepositoryServiceImpl discoveryRepositoryService, DatabaseServerMonitor databaseServerMonitor) {
    super();
    this.discoveryRepositoryService = discoveryRepositoryService;

    // Launch database server monitor on a separate thread:
    Thread databaseMonitorThread = new Thread(() -> databaseServerMonitor.startService());
    databaseMonitorThread.setDaemon(true);
    databaseMonitorThread.start();
    logger.info("DatabaseServer Discovery Service is running!");
  }

  @Override
  public void registerUser(UserData request, StreamObserver<UserData> responseObserver) {
    try {
      // Transmit to the least congested gRPC server:
      int i = 0;
      while(i < numberOfRetries) {
        // Check which gRPC server is least congested:
        logger.info("Forwarding request: Register a user!");
        DatabaseServer leastCongestedServer = discoveryRepositoryService.getDatabaseServerWithLeastCongestion();
        try {
          // Forward request to a proper gRPC database server:
          ManagedChannel channel = channel(leastCongestedServer.getHost(), leastCongestedServer.getPort());
          UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
          UserData response = stub.registerUser(request);

          // Transmit back to client:
          responseObserver.onNext(response);
          responseObserver.onCompleted();
          break;
        } catch (StatusRuntimeException e) {
          i++;
          // Unregister the problematic server and try again
          logger.error("Failed to forward request: Register a user!");
          discoveryRepositoryService.unregisterDatabaseServer(leastCongestedServer);
        }
      }
    } catch (Exception e) {
      responseObserver.onError(Status.INTERNAL.withDescription("Error registering user.").withCause(e).asRuntimeException());
    }
  }


  @Override
  public void getUser(UserNameAndPswd request, StreamObserver<grpc.UserData> responseObserver){
    try {
      // Transmit to the least congested gRPC server:
      int i = 0;
      while(i < numberOfRetries) {
        // Check which gRPC server is least congested:
        logger.info("Forwarding request: Get a user!");
        DatabaseServer leastCongestedServer = discoveryRepositoryService.getDatabaseServerWithLeastCongestion();
        try {
          // Forward request to a proper gRPC database server:
          ManagedChannel channel = channel(leastCongestedServer.getHost(), leastCongestedServer.getPort());
          UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
          UserData response = stub.getUser(request);

          // Transmit back to client:
          responseObserver.onNext(response);
          responseObserver.onCompleted();
          break;
        } catch (StatusRuntimeException e) {
          i++;
          // Unregister the problematic server and try again
          logger.error("Failed to forward request: Get a user!");
          discoveryRepositoryService.unregisterDatabaseServer(leastCongestedServer);
        }
      }
    } catch (NotFoundInDBException e) {
      responseObserver.onError(Status.NOT_FOUND.withDescription("User {" + request.getUsername() + "} not found in DB").withCause(e).asRuntimeException());
    } catch (Exception e) {
      responseObserver.onError(Status.INTERNAL.withDescription("Error fetching User").withCause(e).asRuntimeException());
    }
  }


  private ManagedChannel channel(String host, int port) {
    return ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext()
        .build();
  }
}
