package dk.sep3.dbserverdiscoveryservice.grpc.service;

import dk.sep3.dbserver.model.discoveryService.db_entities.DatabaseServer;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryService;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryServiceImpl;
import dk.sep3.dbserverdiscoveryservice.service.DatabaseServerMonitor;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.PasswordManagerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.naming.ServiceUnavailableException;
import java.util.Arrays;

@GrpcService
public class DbDiscoveryServicePasswordManagerGrpcServiceImpl extends PasswordManagerServiceGrpc.PasswordManagerServiceImplBase
{
  private final DiscoveryRepositoryService discoveryRepositoryService;
  private final int numberOfRetries = 6;
  private static final Logger logger = LoggerFactory.getLogger(DbDiscoveryServicePasswordManagerGrpcServiceImpl.class);

  @Autowired
  public DbDiscoveryServicePasswordManagerGrpcServiceImpl(DiscoveryRepositoryServiceImpl discoveryRepositoryService,
      DatabaseServerMonitor databaseServerMonitor,
      Environment environment) {
    super();
    this.discoveryRepositoryService = discoveryRepositoryService;

    // Launch database server monitor on a separate thread:
    if(databaseServerMonitor != null && !isTestProfile(environment)) {
      Thread databaseMonitorThread = new Thread(databaseServerMonitor::startService);
      databaseMonitorThread.setDaemon(true);
      databaseMonitorThread.start();
      logger.info("DatabaseServer Discovery Service is running!");
    }
  }

  @Override public void handleRequest(GenericRequest request, StreamObserver<GenericResponse> responseObserver) {
    try {
      // Transmit to the least congested gRPC server:
      int i = 0;
      while(i < numberOfRetries) {
        // Check which gRPC server is least congested:
        logger.info("Forwarding request '{}'. ",request.getRequestType());
        DatabaseServer leastCongestedServer = discoveryRepositoryService.getDatabaseServerWithLeastCongestion();
        try {
          // Forward request to a proper gRPC database server:
          ManagedChannel channel = channel(leastCongestedServer.getHost(), leastCongestedServer.getPort());
          PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub stub = PasswordManagerServiceGrpc.newBlockingStub(channel);
          GenericResponse response = stub.handleRequest(request);

          // Transmit back to client:
          responseObserver.onNext(response);
          responseObserver.onCompleted();
          logger.info("Request '{}' was forwarded successfully. Response received successfully and retransmitted back to client.", request.getRequestType());
          break;
        } catch (StatusRuntimeException e) {
          i++;
          // Unregister the problematic server and try again
          logger.error("Failed to forward request '{}' to dbServer. Attempt # {}/{}", request.getRequestType(), i, numberOfRetries);
          discoveryRepositoryService.unregisterDatabaseServer(leastCongestedServer);
          if(i == numberOfRetries) {
            throw new ServiceUnavailableException("Failed to forward request: '" + request.getRequestType() + "', gRPC database service is unavailable.");
          }
        }
      }
    } catch (ServiceUnavailableException e) {
      responseObserver.onError(Status.UNAVAILABLE.withDescription("Error handling Request. gRPC database service is not available.").withCause(e).asRuntimeException());
    } catch (Exception e) {
      responseObserver.onError(Status.INTERNAL.withDescription("Error handling Request. Unspecified cause.").withCause(e).asRuntimeException());
    }
  }

  private ManagedChannel channel(String host, int port) {
    return ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext()
        .build();
  }

  private boolean isTestProfile(Environment environment) {
    // Check if any of the active profiles is "test"
    return Arrays.asList(environment.getActiveProfiles()).contains("test");
  }
}
