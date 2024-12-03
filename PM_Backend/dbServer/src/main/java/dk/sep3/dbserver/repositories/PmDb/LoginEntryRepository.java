package dk.sep3.dbserver.repositories.PmDb;

import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** <p>A Spring Boot Java Persistence API (JPA) @Repository definition.<br>
 * This @Repository interface extends JpaRepository and provides access to paging, sorting and CRUD operations on the database.<br>
 * It is automatically populated by Spring Boot JPA, so no implementation class is needed for the proper Database access operations.<br>
 * JPA specification states that each Entity in the database must have a java annotated @Entity object in the Java code.
 * JPA also states that for each @Entity there must be a corresponding @Repository similar to this.</p> */
@Repository
public interface LoginEntryRepository extends JpaRepository<LoginEntry, Integer> {
  List<LoginEntry> findLoginEntriesByMasterUserId(int masterUserId);
}
