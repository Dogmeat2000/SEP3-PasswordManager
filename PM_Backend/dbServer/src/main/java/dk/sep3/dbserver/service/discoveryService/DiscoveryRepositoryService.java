package dk.sep3.dbserver.service.discoveryService;

import dk.sep3.dbserver.model.discoveryService.db_entities.DatabaseServer;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * <p>Defines the interface responsible for adding this gRPC/database server to the discovery service so external clients may find it</p>
 */
public interface DiscoveryRepositoryService
{
  /** <p>Registers/Updates a Database server in the repository, so clients may find it.</p>
   * @param server The DatabaseServer entity to add to the discovery service.
   * @throws PersistenceException Thrown if registration failed, due to system/persistence issues (i.e. Repository is offline, etc.)
   * @throws DataIntegrityViolationException Thrown if registration failed, due to non-legal information being assigned to the DatabaseServer Entity (i.e. host is null)
   */
  DatabaseServer registerDatabaseServer(DatabaseServer server);


  /** <p>Unregisters a Database server in the repository.</p>
   * @param server The DatabaseServer entity to remove from the discovery service.
   * @throws PersistenceException Thrown if removal failed, due to system/persistence issues (i.e. Repository is offline, etc.)
   * @throws DataIntegrityViolationException Thrown if removal failed, due to non-legal information being assigned to the DatabaseServer Entity (i.e. host is null)
   */
  void unregisterDatabaseServer(DatabaseServer server);


  /** <p>Retrieves the Database server with the oldest ping/timestamp.</p>
   * @return The Database that has not been heard from, the longest.
   * @throws PersistenceException Thrown if query failed, due to system/persistence issues (i.e. Repository is offline, etc.)
   */
  DatabaseServer getDatabaseServerWithOldestPing();


  /** <p>Retrieves the Database server with the least congestion.</p>
   * @return The Database that is least congested.
   * @throws PersistenceException Thrown if query failed, due to system/persistence issues (i.e. Repository is offline, etc.)
   */
  DatabaseServer getDatabaseServerWithLeastCongestion();
}
