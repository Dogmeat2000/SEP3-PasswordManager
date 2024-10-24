package dk.sep3.dbserver.service.passwordManager;

import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * <p>MasterUserRepositoryService defines the interface responsible for the database methods relating to MasterUser registration and management.</p>
 */
public interface MasterUserRepositoryService
{
  /** <p>Registers/Creates a new MasterUser in the repository with the given parameters applied.</p>
   * @param masterUser The MasterUser entity to add to the repository.
   * @return The registered MasterUser instance.
   * @throws PersistenceException Thrown if registration failed, due to system/persistence issues (i.e. Repository is offline, etc.)
   * @throws DataIntegrityViolationException Thrown if registration failed, due to non-legal information being assigned to the MasterUser Entity (i.e. masterUsername is null)
   */
  MasterUser createMasterUser(MasterUser masterUser) throws DataIntegrityViolationException, PersistenceException;


  /** <p>Looks up a MasterUser entity with the specified masterUsername and encrypted password, in the repository</p>
   * @param masterUsername A unique String type masterUsername assigned to the specific MasterUser to fetch.
   * @param encryptedPassword A String type containing the encrypted password associated with the given masterUsername.
   * @return The identified MasterUser instance.
   * @throws NotFoundInDBException Thrown if MasterUser is not found.
   * @throws PersistenceException Thrown if fetching MasterUser failed, due to system/persistence issues (i.e. Repository is offline, etc.)
   * @throws DataIntegrityViolationException Thrown if masterUsername is invalid (i.e. null).
   */
  MasterUser readMasterUser(String masterUsername, String encryptedPassword) throws NotFoundInDBException, DataIntegrityViolationException, PersistenceException;
}
