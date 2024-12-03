package dk.sep3.dbserver.service.Pm;

import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

/**
 * Service implementation for managing LoginEntry entities.
 * Provides methods to create, read, update, and delete LoginEntries.
 */
public interface LoginEntryRepositoryService {
    /**
     * Service implementation for managing LoginEntry entities.
     * Provides methods to create, read, update, and delete LoginEntries.
     */
    LoginEntry createLoginEntry(LoginEntry loginEntry) throws DataIntegrityViolationException, PersistenceException;


    /** <p>Looks up all LoginEntry entities associated with the specified MasterUserId, in the repository</p>
     * @param id A unique int type masterUsername assigned to the specific MasterUser to fetch entries associated with.
     * @return A List of all the LoginEntries associated with the specified MasterUserId.
     * @throws NotFoundInDBException Thrown if no LoginEntry is found.
     * @throws PersistenceException Thrown if fetching LoginEntries failed, due to system/persistence issues (i.e. Repository is offline, etc.)
     * @throws DataIntegrityViolationException Thrown if the provided id is invalid (i.e. 0, or negative).
     */
    List<LoginEntry> readLoginEntriesByMasterUserId(int id) throws NotFoundInDBException, DataIntegrityViolationException, PersistenceException;

    /**
     * Service implementation for managing LoginEntry entities.
     * Provides methods to create, read, update, and delete LoginEntries.
     */
    LoginEntry updateLoginEntry(LoginEntry loginEntry) throws DataIntegrityViolationException, PersistenceException;

    /**
     * Deletes an existing LoginEntry from the database.
     *
     * @param loginEntry The LoginEntry object to be deleted.
     *                   Must have a valid ID and must exist in the database.
     * @throws PersistenceException If the LoginEntry does not exist or if a persistence-related error occurs.
     */
    void deleteLoginEntry(LoginEntry loginEntry) throws PersistenceException;

}
