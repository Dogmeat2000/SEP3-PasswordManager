package dk.sep3.dbserver.grpc.service;

import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommand;
import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommandFactory;
import dk.sep3.dbserver.grpc.factories.GenericResponseFactory;
import dk.sep3.dbserver.service.exceptions.IllegalGrpcCommand;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.PasswordManagerServiceGrpc;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.PersistenceException;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@GrpcService
public class PasswordManagerGrpcServiceImpl extends PasswordManagerServiceGrpc.PasswordManagerServiceImplBase
{
  private static final Logger logger = LoggerFactory.getLogger(PasswordManagerGrpcServiceImpl.class);
  private final GrpcCommandFactory commandManager;

  @Autowired
  public PasswordManagerGrpcServiceImpl(ServerHealthMonitor serverHealthMonitor, GrpcCommandFactory commandManager) {
    super();
    this.commandManager = commandManager;

    // Launch database health monitor on a separate thread:
    if(serverHealthMonitor != null) {
      Thread healthMonitorThread = new Thread(() -> serverHealthMonitor.startService());
      healthMonitorThread.setDaemon(true);
      healthMonitorThread.start();
      logger.info("Server Health Monitoring Service is running!");
    }
  }

  @Override public void handleRequest(GenericRequest request, StreamObserver<GenericResponse> responseObserver){
    try {
      // Validate request
      if(request == null)
        throw new IllegalArgumentException("Request cannot be null");

      // Identify what action was requested:
      String cmdRequested = request.getRequestType();

      // Identify which gRPC command to execute:
      GrpcCommand command = commandManager.getCommand(cmdRequested);

      // Execute the command:
      GenericResponse response = command.execute(request);

      // Transmit response back to client:
      responseObserver.onNext(response);


    } catch (DataIntegrityViolationException e) {
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(400));

    } catch (NotFoundInDBException e) {
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(404));

    } catch (IllegalGrpcCommand e) {
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(405));

    } catch (PersistenceException e) {
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(503));

    } catch (Exception e) {
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(500));

    } finally {
      responseObserver.onCompleted();
    }
  }
}