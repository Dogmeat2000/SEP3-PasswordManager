package dk.sep3.dbserver.service.Pm;

import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import dk.sep3.dbserver.repositories.PmDb.LoginEntryRepository;
import dk.sep3.dbserver.service.exceptions.DuplicateDbEntryException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class LoginEntryRepositoryServiceImpl implements LoginEntryRepositoryService {

    private final LoginEntryRepository loginEntryRepository;
    private static final Logger logger = LoggerFactory.getLogger(LoginEntryRepositoryServiceImpl.class);

    @Autowired
    public LoginEntryRepositoryServiceImpl(LoginEntryRepository loginEntryRepository) {
        this.loginEntryRepository = loginEntryRepository;
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

    private void validateCreateLoginEntry(LoginEntry loginEntry) throws DataIntegrityViolationException {
        if (loginEntry == null) {
            logger.error("Validation failed. LoginEntry is null.");
            throw new DataIntegrityViolationException("LoginEntry cannot be null.");
        }

        validateEntryId(loginEntry.getId());
        validateEntryUsername(loginEntry.getEntryUsername());
        validateEntryPassword(loginEntry.getEntryPassword());
        validateEntryName(loginEntry.getEntryName());
        validateEntryCategoryId(loginEntry.getEntryCategoryId());
        validateMasterUserId(loginEntry.getMasterUserId());

        // Additional validation can be added if there are constraints on uniqueness, etc.
    }

    private void validateEntryId(int id) throws DataIntegrityViolationException {
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
        if (masterUserId <= 0) {
            logger.error("Validation failed. MasterUser ID associated with LoginEntry must be a positive integer.");
            throw new DataIntegrityViolationException("MasterUser ID must be a positive integer.");
        }
    }
}
