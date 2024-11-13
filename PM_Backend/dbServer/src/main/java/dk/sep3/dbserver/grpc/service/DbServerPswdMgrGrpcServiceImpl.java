package dk.sep3.dbserver.grpc.service;

import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommand;
import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommandFactory;
import dk.sep3.dbserver.grpc.factories.GenericResponseFactory;
import dk.sep3.dbserver.service.exceptions.DuplicateDbEntryException;
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
public class DbServerPswdMgrGrpcServiceImpl extends PasswordManagerServiceGrpc.PasswordManagerServiceImplBase
{
  private static final Logger logger = LoggerFactory.getLogger(DbServerPswdMgrGrpcServiceImpl.class);
  private final GrpcCommandFactory commandManager;

  @Autowired
  public DbServerPswdMgrGrpcServiceImpl(ServerHealthMonitor serverHealthMonitor, GrpcCommandFactory commandManager) {
    super();
    this.commandManager = commandManager;

    // Launch database health monitor on a separate thread:
    if(serverHealthMonitor != null) {
      Thread healthMonitorThread = new Thread(serverHealthMonitor::startService);
      healthMonitorThread.setDaemon(true);
      healthMonitorThread.start();
      logger.info("Server Health Monitoring Service is running!");
    }
  }

  @Override public void handleRequest(GenericRequest request, StreamObserver<GenericResponse> responseObserver){
    try {
      // Validate request
      if(request == null || request.getRequestType().isBlank() || request.getRequestType().isEmpty())
        throw new IllegalArgumentException("Request cannot be null");

      // Identify what action was requested:
      String cmdRequested = request.getRequestType();

      // Identify which gRPC command to execute:
      GrpcCommand command = commandManager.getCommand(cmdRequested);

      // Execute the command:
      GenericResponse response = command.execute(request);

      // Transmit response back to client:
      responseObserver.onNext(response);

    } catch (DataIntegrityViolationException | IllegalArgumentException e) {
      // Query contained an Illegal Argument (example: attempt to create user without providing username.)
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(400, e.getMessage()));
      logError(400, e.getMessage());

    } catch (NotFoundInDBException e) {
      // Could Not find value in repository
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(404, e.getMessage()));
      logError(404, e.getMessage());

    } catch (IllegalGrpcCommand e) {
      // Illegal Grpc Command provided
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(405, e.getMessage()));
      logError(405, e.getMessage());

    }  catch (DuplicateDbEntryException e) {
      // Value already exists in repository (example: attempting to create a user that already exists)
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(409, e.getMessage()));
      logError(409, e.getMessage());

    } catch (PersistenceException e) {
      // Service unavailable
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(503, e.getMessage()));
      logError(503, e.getMessage());

    } catch (Exception e) {
      // Internal Server Error
      responseObserver.onNext(GenericResponseFactory.buildGrpcGenericResponseWithError(500, e.getMessage()));
      logError(500, e.getMessage());

    } finally {
      responseObserver.onCompleted();
    }
  }


  private void logError(int statusCode, String errorMsg) {
    logger.error("Failed to process request. ErrorCode '{}' was thrown. Reason: '{}'", statusCode, errorMsg);
  }
}