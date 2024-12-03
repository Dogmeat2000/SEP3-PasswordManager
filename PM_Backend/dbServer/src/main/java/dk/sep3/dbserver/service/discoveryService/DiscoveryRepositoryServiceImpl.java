package dk.sep3.dbserver.service.discoveryService;

import dk.sep3.dbserver.model.discoveryService.db_entities.DatabaseServer;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * <p>Implementation responsible for adding this gRPC/database server to the discovery service so external clients may find it</p>
 */
@Service
public class DiscoveryRepositoryServiceImpl implements DiscoveryRepositoryService
{
  public final DiscoveryRepository serverRepository;
  private static final Logger logger = LoggerFactory.getLogger(DiscoveryRepositoryServiceImpl.class);
  private final int numberOfRetries = 10;

  @Autowired
  public DiscoveryRepositoryServiceImpl(DiscoveryRepository serverRepository) {
    this.serverRepository = serverRepository;
  }


  @Override public DatabaseServer registerDatabaseServer(DatabaseServer server) throws DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    if(!validateHost(server))
      throw new DataIntegrityViolationException("Invalid DatabaseServer provided. Incompatible with database!");

    // Attempt to add DatabaseServer to DB:
    try {
      int i = 0;
      // Try a few times until timeout is reached.
      while(i <= numberOfRetries) {
        try {

          // Add this server to the database:
          DatabaseServer serverResponse = serverRepository.save(server);
          logger.info("DatabaseServer added to Discovery DB with ID: {}", serverResponse.getId());
          return serverResponse;
        } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
          logger.error("Unable to register DatabaseServer in DB with address: {}, Reason: {}", server.getHost(), e.getMessage());
          throw new DataIntegrityViolationException("Invalid DatabaseServer provided. Incompatible with database!");
        } catch (Exception e) {
          Thread.sleep(500);
          i++;
        }
      }
    } catch (DataIntegrityViolationException e) {
      logger.error("Unable to register DatabaseServer in DB with address: {}, Reason: {}", server.getHost(), e.getMessage());
      throw new DataIntegrityViolationException("Invalid PartType DatabaseServer. Incompatible with database! Reason: " + e.getMessage());

    } catch (PersistenceException | InterruptedException e) {
      logger.error("Persistence exception occurred while registering DatabaseServer at address {}: {}", server.getHost(), e.getMessage());
      throw new PersistenceException(e);
    }
    return null;
  }


  @Override public void unregisterDatabaseServer(DatabaseServer server) throws DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    if(!validateHost(server))
      throw new DataIntegrityViolationException("Invalid DatabaseServer provided. Incompatible with database!");

    // Attempt to remove the DatabaseServer from the DB:
    try {
      int i = 0;
      // Try a few times until timeout is reached.
      while(i <= numberOfRetries) {
        try {
          serverRepository.delete(server);
          logger.info("DatabaseServer removed from Discovery DB with address: {}", server.getHost() + ":" + server.getPort());
          break;
        }
        catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
          logger.error("Unable to delete DatabaseServer from DB with address: {}, Reason: {}", server.getHost(), e.getMessage());
          throw new DataIntegrityViolationException("Invalid DatabaseServer provided. Incompatible with database!");
        }
        catch (Exception e) {
          Thread.sleep(500);
          i++;
        }
      }
    } catch (DataIntegrityViolationException e) {
      logger.error("Unable to delete DatabaseServer from DB with address: {}, Reason: {}", server.getHost(), e.getMessage());
      throw new DataIntegrityViolationException("Invalid PartType DatabaseServer. Incompatible with database!");

    } catch (PersistenceException | InterruptedException e) {
      logger.error("Persistence exception occurred while removing a DatabaseServer from db with address {}: {}", server.getHost(), e.getMessage());
      throw new PersistenceException(e);
    }
  }


  @Override public DatabaseServer getDatabaseServerWithOldestPing() throws NotFoundInDBException, PersistenceException {
    // Attempt to get the DatabaseServer that is the oldest:
    try {
      DatabaseServer server = serverRepository.findFirstByOrderByLastPingAsc().orElseThrow(() -> new NotFoundInDBException("No DatabaseServers found"));

      logger.info("Oldest DatabaseServer found in Discovery DB with address: {} and last ping from: {}", server.getHost(), server.getLastPing());
      return server;

    } catch (DataIntegrityViolationException e) {
      logger.error("Failed to find the oldest DatabaseServer in the db");
      throw new DataIntegrityViolationException("Failed to find the oldest DatabaseServer in the db");

    } catch (PersistenceException e) {
      logger.error("Persistence exception occurred while looking for the oldest DatabaseServer in the db");
      throw new PersistenceException(e);
    }
  }


  @Override public DatabaseServer getDatabaseServerWithLeastCongestion() throws NotFoundInDBException, PersistenceException {
    // Attempt to get the DatabaseServer that is least congested:
    try {
      DatabaseServer server = serverRepository.findFirstByOrderByCongestionPercentageAsc().orElseThrow(() -> new NotFoundInDBException("No DatabaseServers found"));

      logger.info("Least Congested DatabaseServer found in Discovery DB with address: {} and last ping from: {}", server.getHost(), server.getLastPing());
      return server;

    } catch (DataIntegrityViolationException e) {
      logger.error("Failed to find the least congested DatabaseServer in the db");
      throw new DataIntegrityViolationException("Failed to find the oldest DatabaseServer in the db");

    } catch (PersistenceException e) {
      logger.error("Persistence exception occurred while looking for the least congested DatabaseServer in the db");
      throw new PersistenceException(e);
    }
  }


  private boolean validateHost(DatabaseServer server){
    // Check that server is not null:
    if(server == null) {
      logger.error("Failed validation because server is null");
      return false;
    }


    // Check that none of the host-attributes are null:
    if(server.getHost() == null
        || server.getPort() == 0) {
      logger.error("Failed validation because server has either null host or null port");
      return false;
    }

    // Host has been validated:
    return true;
  }
}
