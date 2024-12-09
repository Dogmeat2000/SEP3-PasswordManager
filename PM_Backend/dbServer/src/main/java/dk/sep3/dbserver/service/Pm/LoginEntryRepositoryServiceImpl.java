package dk.sep3.dbserver.service.Pm;

import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import dk.sep3.dbserver.repositories.PmDb.LoginEntryRepository;
import dk.sep3.dbserver.repositories.PmDb.MasterUserRepository;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>Class implements the interface responsible for the database methods relating to LoginEntry registration and management.</p>
 */
@Service
public class LoginEntryRepositoryServiceImpl implements LoginEntryRepositoryService {

    private final LoginEntryRepository loginEntryRepository;
    private static final Logger logger = LoggerFactory.getLogger(LoginEntryRepositoryServiceImpl.class);
    private final MasterUserRepository masterUserRepository;

    @Autowired
    public LoginEntryRepositoryServiceImpl(LoginEntryRepository loginEntryRepository, MasterUserRepository masterUserRepository) {
        this.loginEntryRepository = loginEntryRepository;
        this.masterUserRepository = masterUserRepository;
    }

    @Override
    public LoginEntry createLoginEntry(LoginEntry loginEntry) throws DataIntegrityViolationException, PersistenceException {
        // Validate the received LoginEntry data before saving to the database
        validateCreateLoginEntry(loginEntry);

        try {
            // Save LoginEntry to the database
            LoginEntry newLoginEntry = loginEntryRepository.save(loginEntry);
            logger.info("LoginEntry '{}' added to DB", newLoginEntry.getEntryName());
            return newLoginEntry;

        } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
            logger.error("Unable to create LoginEntry '{}', Reason: '{}'", loginEntry.getEntryName(), e.getMessage());
            throw new DataIntegrityViolationException("Invalid LoginEntry data provided. Reason: " + e.getMessage());

        } catch (PersistenceException e) {
            logger.error("Persistence exception occurred while creating LoginEntry '{}', Reason: '{}'", loginEntry.getEntryName(), e.getMessage());
            throw new PersistenceException(e);
        }
    }


    @Override public List<LoginEntry> readLoginEntriesByMasterUserId(int id) throws NotFoundInDBException, DataIntegrityViolationException, PersistenceException {
        // Validate the id:
        if(id <= 0)
            throw new DataIntegrityViolationException("Invalid MasterUserDTO provided. MasterUserId is zero or negative.");

        // Attempt to fetch LoginEntries from DB:
        try {
            // Causes the repository to query the database. If no match is found, an error is thrown immediately.
            List<LoginEntry> entriesFound = loginEntryRepository.findLoginEntriesByMasterUserId(id);
            if(entriesFound.isEmpty())
                throw new NotFoundInDBException("No LoginEntries associated with MasterUserId {" + id + "} found in DB");

            logger.info("'{}' LoginEntries read from DB", entriesFound.size());
            return entriesFound;

        } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
            // Handle exceptions caused by incompatible data formats (either java or sql ddl mismatch in definitions):
            logger.error("Unable to fetch LoginEntries associated with MasterUserId '{}' in DB, Reason: {}", id, e.getMessage());
            throw new DataIntegrityViolationException("Invalid MasterUserId provided. Incompatible with database! Reason: " + e.getMessage());

        } catch (NoSuchElementException e) {
            // No LoginEntries were found.
            throw new NotFoundInDBException(e.getMessage());

        } catch (PersistenceException e) {
            // Handle other exceptions relating to persistence and connectivity:
            logger.error("Persistence exception occurred while fetching all LoginEntries associated with MasterUserId '{}', Reason: {}", id, e.getMessage());
            throw new PersistenceException(e);
        }
    }


    @Override
    public LoginEntry updateLoginEntry(LoginEntry loginEntry) throws DataIntegrityViolationException, PersistenceException {
        // Validating LoginEntry
        validateLoginEntry(loginEntry);

        try {
            // checks if the entry exists in DB
            if (!loginEntryRepository.existsById(loginEntry.getId())) {
                logger.error("LoginEntry with ID '{}' not found in DB. Update operation aborted.", loginEntry.getId());
                throw new PersistenceException("LoginEntry with ID " + loginEntry.getId() + " not found in the database.");
            }

            // Delete the LoginEntry by id
            LoginEntry updatedLoginEntry = loginEntryRepository.save(loginEntry);
            logger.info("LoginEntry '{}' updated in DB", loginEntry.getEntryName());
            return updatedLoginEntry;

        } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
            logger.error("Unable to update LoginEntry '{}', Reason: '{}'", loginEntry.getEntryName(), e.getMessage());
            throw new DataIntegrityViolationException("Invalid LoginEntry data provided. Reason: " + e.getMessage());
        } catch (PersistenceException e) {
            logger.error("Persistence exception occurred while creating LoginEntry '{}', Reason: '{}'", loginEntry.getEntryName(), e.getMessage());
            throw new PersistenceException(e);
        }
    }

    @Override
    public void deleteLoginEntry(LoginEntry loginEntry) throws PersistenceException {
        // Validating LoginEntry
        validateLoginEntry(loginEntry);

        try {
            // checks if the entry exists in DB
            if (!loginEntryRepository.existsById(loginEntry.getId())) {
                logger.error("LoginEntry with ID '{}' not found in DB. Delete operation aborted.", loginEntry.getId());
                throw new PersistenceException("LoginEntry with ID " + loginEntry.getId() + " not found in the database.");
            }

            // Delete the LoginEntry by id
            loginEntryRepository.deleteById(loginEntry.getId());
            logger.info("LoginEntry with ID '{}' deleted from DB", loginEntry.getId());

        } catch (IllegalArgumentException e) {
            logger.error("Unable to delete LoginEntry '{}', Reason: '{}'", loginEntry.getEntryName(), e.getMessage());
            throw new PersistenceException("Invalid LoginEntry data provided for deletion. Reason: " + e.getMessage());
        } catch (PersistenceException e) {
            logger.error("Persistence exception occurred while deleting LoginEntry '{}', Reason: '{}'", loginEntry.getEntryName(), e.getMessage());
            throw new PersistenceException(e);
        }
    }

    private void validateLoginEntry(LoginEntry loginEntry) throws DataIntegrityViolationException {
        if (loginEntry == null) {
            logger.error("Validation failed. LoginEntry is null.");
            throw new DataIntegrityViolationException("LoginEntry cannot be null.");
        }

        validateEntryUsername(loginEntry.getEntryUsername());
        validateEntryPassword(loginEntry.getEntryPassword());
        validateEntryName(loginEntry.getEntryName());
        validateEntryCategoryId(loginEntry.getEntryCategoryId());
        validateMasterUserId(loginEntry.getMasterUserId());

        // Additional validation can be added if there are constraints on uniqueness, etc.
    }

    private void validateCreateLoginEntry(LoginEntry loginEntry) throws DataIntegrityViolationException {
        if (loginEntry == null) {
            logger.error("Validation failed. LoginEntry is null.");
            throw new DataIntegrityViolationException("LoginEntry cannot be null.");
        }

        validateEntryIdOnCreate(loginEntry.getId());
        validateEntryUsername(loginEntry.getEntryUsername());
        validateEntryPassword(loginEntry.getEntryPassword());
        validateEntryName(loginEntry.getEntryName());
        validateEntryCategoryId(loginEntry.getEntryCategoryId());
        validateMasterUserId(loginEntry.getMasterUserId());

        // Additional validation can be added if there are constraints on uniqueness, etc.
    }

    private void validateEntryIdOnCreate(int id) throws DataIntegrityViolationException {
        if (id != 0) {
            logger.error("Validation failed. Provided LoginEntry ID must be zero, as the DB auto-generates it.");
            throw new DataIntegrityViolationException("LoginEntry ID must be zero as the DB auto-generates it.");
        }
    }

    private void validateEntryUsername(String entryUsername) throws DataIntegrityViolationException {
        if (entryUsername == null || entryUsername.isBlank()) {
            logger.error("Validation failed. LoginEntry username cannot be null or blank.");
            throw new DataIntegrityViolationException("LoginEntry username cannot be null or blank.");
        }
    }

    private void validateEntryPassword(String entryPassword) throws DataIntegrityViolationException {
        if (entryPassword == null || entryPassword.isBlank()) {
            logger.error("Validation failed. LoginEntry password cannot be null or blank.");
            throw new DataIntegrityViolationException("LoginEntry password cannot be null or blank.");
        }
    }

    private void validateEntryName(String entryName) throws DataIntegrityViolationException {
        if (entryName == null || entryName.isBlank()) {
            logger.error("Validation failed. LoginEntry name cannot be null or blank.");
            throw new DataIntegrityViolationException("LoginEntry name cannot be null or blank.");
        }
    }

    private void validateEntryCategoryId(int entryCategoryId) throws DataIntegrityViolationException {
        if (entryCategoryId <= 0) {
            logger.error("Validation failed. LoginEntry category ID must be a positive integer.");
            throw new DataIntegrityViolationException("LoginEntry category ID must be a positive integer.");
        }
    }

    private void validateMasterUserId(int masterUserId) throws DataIntegrityViolationException {
        if (masterUserId <= 0 || !masterUserExists(masterUserId)) {
            logger.error("Validation failed. MasterUser ID associated with LoginEntry must be a valid and existing positive integer. masterUserId " + masterUserId);
            throw new DataIntegrityViolationException("MasterUser ID must be a positive integer and must exist.");
        }
    }

    private boolean masterUserExists(int masterUserId) {
        return masterUserRepository.existsById(masterUserId);
    }
}
