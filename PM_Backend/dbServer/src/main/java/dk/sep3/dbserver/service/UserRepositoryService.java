package dk.sep3.dbserver.service;

import dk.sep3.dbserver.db_entities.User;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * <p>UserRepositoryService defines the interface responsible for the database methods relating to User registration and management.</p>
 */
public interface UserRepositoryService
{
  /** <p>Registers/Creates a new User in the repository with the given parameters applied.</p>
   * @param user The User entity to add to the repository.
   * @return The registered User instance.
   * @throws PersistenceException Thrown if registration failed, due to system/persistence issues (i.e. Repository is offline, etc.)
   * @throws DataIntegrityViolationException Thrown if registration failed, due to non-legal information being assigned to the User Entity (i.e. username is null)
   */
  User registerUser(User user) throws DataIntegrityViolationException, PersistenceException;


  /** <p>Looks up a User entity with the specified username and password, in the repository</p>
   * @param username A unique String type username assigned to the specific User to fetch.
   * @param encryptedPassword A String type containing the encrypted password associated with the given username.
   * @return The identified User instance.
   * @throws NotFoundInDBException Thrown if User is not be found.
   * @throws PersistenceException Thrown if fetching User failed, due to system/persistence issues (i.e. Repository is offline, etc.)
   * @throws DataIntegrityViolationException Thrown if username is invalid (i.e. null).
   */
  User fetchUser(String username, String encryptedPassword) throws NotFoundInDBException, DataIntegrityViolationException, PersistenceException;
}
