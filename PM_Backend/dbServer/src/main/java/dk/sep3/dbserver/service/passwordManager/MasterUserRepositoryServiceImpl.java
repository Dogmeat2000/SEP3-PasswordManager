package dk.sep3.dbserver.service.passwordManager;

import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;
import dk.sep3.dbserver.repositories.passwordManagerDb.MasterUserRepository;
import dk.sep3.dbserver.service.exceptions.DuplicateDbEntryException;
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

@Service
public class MasterUserRepositoryServiceImpl implements MasterUserRepositoryService
{
  public final MasterUserRepository masterUserRepository;
  private static final Logger logger = LoggerFactory.getLogger(MasterUserRepositoryServiceImpl.class);

  @Autowired
  public MasterUserRepositoryServiceImpl(MasterUserRepository masterUserRepository) {
    this.masterUserRepository = masterUserRepository;
  }

  @Override public MasterUser createMasterUser(MasterUser masterUser) throws DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    validateCreateMasterUser(masterUser);

    // Attempt to add MasterUser to DB:
    try {
      MasterUser newMasterUser = masterUserRepository.save(masterUser);
      logger.info("MasterUser '{}' added to DB", newMasterUser.getMasterUsername());
      return newMasterUser;

    } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
      // Handle exceptions caused by incompatible data formats (either java or sql ddl mismatch in definitions):
      logger.error("Unable to register MasterUser '{}' in DB, Reason: '{}'", masterUser.getMasterUsername(), e.getMessage());
      throw new DataIntegrityViolationException("Invalid MasterUser provided. Incompatible with database! Reason: " + e.getMessage());

    } catch (PersistenceException e) {
      // Handle other exceptions relating to persistence and connectivity:
      logger.error("Persistence exception occurred while registering MasterUser {}, Reason: {}", masterUser.getMasterUsername(), e.getMessage());
      throw new PersistenceException(e);
    }
  }


  @Override public MasterUser readMasterUser(String masterUsername, String encryptedPassword) throws NotFoundInDBException, DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    validateMasterUserName(masterUsername);
    validateMasterUserPassword(encryptedPassword);

    // Attempt to fetch MasterUser from DB:
    try {
      // Causes the repository to query the database. If no match is found, an error is thrown immediately.
      List<MasterUser> masterUsersFound = masterUserRepository.findByMasterUsernameAndEncryptedPassword(masterUsername, encryptedPassword);
      if(masterUsersFound.isEmpty())
        throw new NotFoundInDBException("MasterUser {" + masterUsername + "} not found in DB");
      else if (masterUsersFound.size() > 1)
        throw new DataIntegrityViolationException("Multiple matching masterUsers found in database. Only one matching masterUser is allowed");

      logger.info("MasterUser {} read from DB", masterUsername);
      return masterUsersFound.getFirst();

    } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
      // Handle exceptions caused by incompatible data formats (either java or sql ddl mismatch in definitions):
      logger.error("Unable to fetch MasterUser {} in DB, Reason: {}", masterUsername, e.getMessage());
      throw new DataIntegrityViolationException("Invalid MasterUser provided. Incompatible with database! Reason: " + e.getMessage());

    } catch (NoSuchElementException e) {
      // No MasterUser was found.
      throw new NotFoundInDBException(e.getMessage());

    } catch (PersistenceException e) {
      // Handle other exceptions relating to persistence and connectivity:
      logger.error("Persistence exception occurred while fetching MasterUser {}, Reason: {}", masterUsername, e.getMessage());
      throw new PersistenceException(e);
    }
  }


  private void validateCreateMasterUser(MasterUser masterUser) throws DataIntegrityViolationException, DuplicateDbEntryException {
    if(masterUser == null) {
      logger.error("Could not Create new MasterUser in DB., Validation of MasterUser failed. MasterUser is null.");
      throw new DataIntegrityViolationException("Validation of MasterUser failed. MasterUser is null.");
    }

    validateMasterUserName(masterUser.getMasterUsername());
    validateMasterUserPassword(masterUser.getEncryptedPassword());

    // Check if the MasterUser already exists in the database:
    List<MasterUser> masterUsersFound = masterUserRepository.findByMasterUsername(masterUser.getMasterUsername());

    if(!masterUsersFound.isEmpty()){
      logger.error("Could not Create new MasterUser in DB., MasterUser with name '{}' already exists in DB", masterUser.getMasterUsername());
      throw new DuplicateDbEntryException("MasterUser '" + masterUser.getMasterUsername() + "' already exists in DB");
    }

    // Validation passed
  }


  private void validateMasterUserName(String masterUserName) throws DataIntegrityViolationException {
    if(masterUserName == null){
      logger.error("Validation of MasterUserName failed. MasterUsername cannot be null.");
      throw new DataIntegrityViolationException("MasterUsername cannot be null");
    } else if(masterUserName.isEmpty() || masterUserName.isBlank()) {
      logger.error("Validation of MasterUserName failed. MasterUsername cannot be empty.");
      throw new DataIntegrityViolationException("MasterUsername cannot be empty");
    }

    // Validation passed.
  }


  private void validateMasterUserPassword(String encryptedPassword) throws DataIntegrityViolationException {
    if(encryptedPassword == null) {
      logger.error("Validation of MasterUsers encryptedPassword' failed. Encrypted Password cannot be null");
      throw new DataIntegrityViolationException("Encrypted Password cannot be null");
    } else if(encryptedPassword.isEmpty() || encryptedPassword.isBlank()){
      logger.error("Validation of MasterUsers encryptedPassword' failed. Encrypted Password cannot be empty");
      throw new DataIntegrityViolationException("Encrypted Password cannot be empty");
    } else if (encryptedPassword.length() < 12) {
      logger.error("Validation of MasterUsers encryptedPassword' failed. Encrypted Password must be at least 12 characters long");
      throw new DataIntegrityViolationException("Encrypted Password must be at least 12 characters");
    }

    // Validation passed.
  }
}
