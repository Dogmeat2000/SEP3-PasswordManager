package dk.sep3.dbserver.repositories.passwordManagerDb;

import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** <p>A Spring Boot Java Persistence API (JPA) @Repository definition.<br>
 * This @Repository interface extends JpaRepository and provides access to paging, sorting and CRUD operations on the database.<br>
 * It is automatically populated by Spring Boot JPA, so no implementation class is needed for the proper Database access operations.<br>
 * JPA specification states that each Entity in the database must have a java annotated @Entity object in the Java code.
 * JPA also states that for each @Entity there must be a corresponding @Repository similar to this.</p> */
@Repository
public interface MasterUserRepository extends JpaRepository<MasterUser, Integer>  // <-- Primary key of Entity must be provided as the Type to JpaRepository!
{
  // The extended JpaRepository automatically adds CRUD and Paging/Sorting operations to the User entity.
  // If additional functionality is required, it can be added below along with custom SQL queries.

  /** <p>Fetches a List of users with this exact username and password from the database.<br><br>
   * This method makes use of JPA's built in query derivation, where JPA automatically translates
   * and builds the SQL query based on the methods name.<br>
   * Note: Be sure to follow JPA naming conventions: <a href="https://www.baeldung.com/spring-data-derived-queries">https://www.baeldung.com/spring-data-derived-queries</a></p>
   * @param username Name of the user to find.
   * @param password The encrypted password associated with the given username.
   * @return A list of all the users found matching the given attributes.*/
  List<MasterUser> findByMasterUsernameAndEncryptedPassword(String username, String password);
}
