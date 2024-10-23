package dk.sep3.dbserver.service.passwordManager;

import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;
import dk.sep3.dbserver.repositories.passwordManagerDb.MasterUserRepository;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterUserRepositoryServiceImpl implements MasterUserRepositoryService
{
  public final MasterUserRepository masterUserRepository;
  private static final Logger logger = LoggerFactory.getLogger(MasterUserRepositoryServiceImpl.class);

  @Autowired
  public MasterUserRepositoryServiceImpl(MasterUserRepository masterUserRepository) {
    this.masterUserRepository = masterUserRepository;
  }

  @Override public MasterUser registerMasterUser(MasterUser masterUser) throws DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    if(masterUser == null || masterUser.getMasterUsername() == null || masterUser.getEncryptedPassword() == null)
      throw new DataIntegrityViolationException("MasterUser cannot be null");

    // Attempt to add MasterUser to DB:
    try {
      MasterUser newMasterUser = masterUserRepository.save(masterUser);
      logger.info("MasterUser {} added to DB", newMasterUser.getMasterUsername());
      return newMasterUser;

    } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
      // Handle exceptions caused by incompatible data formats (either java or sql ddl mismatch in definitions):
      logger.error("Unable to register MasterUser {} in DB, Reason: {}", masterUser.getMasterUsername(), e.getMessage());
      throw new DataIntegrityViolationException("Invalid MasterUser provided. Incompatible with database!");

    } catch (PersistenceException e) {
      // Handle other exceptions relating to persistence and connectivity:
      logger.error("Persistence exception occurred while registering MasterUser {}, Reason: {}", masterUser.getMasterUsername(), e.getMessage());
      throw new PersistenceException(e);
    }
  }

  @Override public MasterUser fetchMasterUser(String masterUsername, String encryptedPassword) throws NotFoundInDBException, DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    if(masterUsername == null)
      throw new DataIntegrityViolationException("MasterUsername cannot be null");

    // Attempt to fetch MasterUser from DB:
    try {
      // Causes the repository to query the database. If no match is found, an error is thrown immediately.
      List<MasterUser> masterUsersFound = masterUserRepository.findByMasterUsernameAndEncryptedPassword(masterUsername, encryptedPassword);
      if(masterUsersFound.isEmpty())
        throw new NotFoundInDBException("MasterUser {" + masterUsername + "} not found in DB");
      else if (masterUsersFound.size() > 1)
        throw new DataIntegrityViolationException("Multiple matching masterUsers found in database. Only one matching masterUser is allowed");

      logger.info("MasterUser {} read from DB", masterUsername);
      return masterUsersFound.get(0); // Assuming List.getFirst() should be replaced with get(0) for Java List.

    } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
      // Handle exceptions caused by incompatible data formats (either java or sql ddl mismatch in definitions):
      logger.error("Unable to fetch MasterUser {} in DB, Reason: {}", masterUsername, e.getMessage());
      throw new DataIntegrityViolationException("Invalid MasterUsername provided. Incompatible with database!");

    } catch (PersistenceException e) {
      // Handle other exceptions relating to persistence and connectivity:
      logger.error("Persistence exception occurred while fetching MasterUser {}, Reason: {}", masterUsername, e.getMessage());
      throw new PersistenceException(e);
    }
  }
}
