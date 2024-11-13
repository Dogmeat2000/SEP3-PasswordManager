package dk.sep3.dbserver.repositories.PmDb;

import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginEntryRepository extends JpaRepository<LoginEntry, Integer> {
  List<LoginEntry> findLoginEntriesByMasterUserId(int masterUserId);
}
