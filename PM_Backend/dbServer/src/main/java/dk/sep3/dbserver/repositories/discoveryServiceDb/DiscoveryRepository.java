package dk.sep3.dbserver.repositories.discoveryServiceDb;

import dk.sep3.dbserver.model.discoveryService.db_entities.DatabaseServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscoveryRepository extends JpaRepository<DatabaseServer, Long>
{
  // The extended JpaRepository automatically adds CRUD and Paging/Sorting operations to the DatabaseServer entity.
  // If additional functionality is required, it can be added below along with custom SQL queries.
  /** Find the Database server that has not been heard from, the longest. */
  Optional<DatabaseServer> findFirstByOrderByLastPingAsc();

  /** Find the Database server, that is least congested */
  Optional<DatabaseServer> findFirstByOrderByCongestionPercentageAsc();
}
