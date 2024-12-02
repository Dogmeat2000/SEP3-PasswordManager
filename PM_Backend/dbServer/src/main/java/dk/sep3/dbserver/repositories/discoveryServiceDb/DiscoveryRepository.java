package dk.sep3.dbserver.repositories.discoveryServiceDb;

import dk.sep3.dbserver.model.discoveryService.db_entities.DatabaseServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscoveryRepository extends JpaRepository<DatabaseServer, Long> // <-- Primary key of Entity must be provided as the Type to JpaRepository!
{
  /** <p>Find the Database server that has not been heard from the longest.</p>
   * @return An Optional value, that is either Null - meaning no database server was found - or a DatabaseServer entity corresponding to the server that had the highest/oldest ping.*/
  Optional<DatabaseServer> findFirstByOrderByLastPingAsc();

  /** <p>Find the Database server, that is least congested</p>
   * @return An Optional value, that is either Null - meaning no database server was found - or a DatabaseServer entity corresponding to the server that is experiencing the least gRPC traffic.*/
  Optional<DatabaseServer> findFirstByOrderByCongestionPercentageAsc();
}
