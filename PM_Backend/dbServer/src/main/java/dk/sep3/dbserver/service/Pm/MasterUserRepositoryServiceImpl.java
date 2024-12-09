package dk.sep3.dbserver.service.Pm;

import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import dk.sep3.dbserver.repositories.PmDb.MasterUserRepository;
import dk.sep3.dbserver.service.exceptions.DuplicateDbEntryException;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>Class implements the interface responsible for the database methods relating to MasterUser registration and management.</p>
 */
@Service
public class MasterUserRepositoryServiceImpl implements MasterUserRepositoryService
{
  private final MasterUserRepository masterUserRepository;
  private final PasswordEncoder passwordEncoder;
  private static final Logger logger = LoggerFactory.getLogger(MasterUserRepositoryServiceImpl.class);

  @Autowired
  public MasterUserRepositoryServiceImpl(MasterUserRepository masterUserRepository, PasswordEncoder passwordEncoder) {
    this.masterUserRepository = masterUserRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override public MasterUser createMasterUser(MasterUser masterUser) throws DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    validateCreateMasterUser(masterUser);

    // Hash Password, before committing to DB:
    String unencryptedPassword = masterUser.getEncryptedPassword();
    String encryptedPassword = passwordEncoder.encode(unencryptedPassword);
    masterUser.setEncryptedPassword(encryptedPassword);

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
      List<MasterUser> masterUsersFound = masterUserRepository.findByMasterUsername(masterUsername);
      if(masterUsersFound.isEmpty())
        throw new NotFoundInDBException("MasterUser {" + masterUsername + "} not found in DB");
      else if (masterUsersFound.size() > 1)
        throw new DataIntegrityViolationException("Multiple matching masterUsers found in database. Only one matching masterUser is allowed");

      logger.info("MasterUser {} read from DB", masterUsername);

      // Check that the found MasterUser has a password that matches the provided:
      MasterUser foundMasterUser = masterUsersFound.getFirst();

      boolean validationSuccess;
      if(encryptedPassword.contains("argon2id") && encryptedPassword.contains("$m=") && encryptedPassword.contains(",t=") && encryptedPassword.contains(",p=")){
        // Received password is already encrypted. Use regular equals method:
        validationSuccess = encryptedPassword.equals(foundMasterUser.getEncryptedPassword());
      } else {
        // Password is encrypted. We must encrypt it and then match it:
        validationSuccess = passwordEncoder.matches(encryptedPassword, foundMasterUser.getEncryptedPassword());
      }

      if(validationSuccess) {
        // Return the result:
        logger.info("MasterUser {} validated successfully", masterUsername);
        return masterUsersFound.getFirst();
      } else {
        logger.error("MasterUser {} failed validation. Wrong password provided.", masterUsername);
        throw new NotFoundInDBException("MasterUser {" + masterUsername + "} found, but invalid password was provided");
      }

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

    validateMasterUserId(masterUser.getId());
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
    } else if(masterUserName.isBlank()) {
      logger.error("Validation of MasterUserName failed. MasterUsername cannot be empty.");
      throw new DataIntegrityViolationException("MasterUsername cannot be empty");
    }

    // Validation passed.
  }


  private void validateMasterUserPassword(String encryptedPassword) throws DataIntegrityViolationException {
    if(encryptedPassword == null) {
      logger.error("Validation of MasterUsers encryptedPassword' failed. Password cannot be null");
      throw new DataIntegrityViolationException("Encrypted Password cannot be null");
    } else if(encryptedPassword.isBlank()){
      logger.error("Validation of MasterUsers encryptedPassword' failed. Password cannot be empty");
      throw new DataIntegrityViolationException("Encrypted Password cannot be empty");
    } else if (encryptedPassword.length() < 12) {
      logger.error("Validation of MasterUsers encryptedPassword' failed. Password must be at least 12 characters long");
      throw new DataIntegrityViolationException("Encrypted Password must be at least 12 characters");
    }

    // Validation passed.
  }


  private void validateMasterUserId(int id) throws DataIntegrityViolationException {
    if(id != 0) {
      logger.error("Validation of MasterUser id' failed. Provided masterUser Id must be zero, since db handles finding the id.");
      throw new DataIntegrityViolationException("Provided masterUser Id must be zero, since db handles finding the id.");
    }

    // Validation passed.
  }
}
