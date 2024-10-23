package dk.sep3.dbserver.service.passwordManager;

import dk.sep3.dbserver.model.passwordManager.db_entities.User;
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


  @Override public User createUser(User user) throws DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    if(user == null || user.getUsername() == null || user.getEncryptedPassword() == null)
      throw new DataIntegrityViolationException("User cannot be null");

    // Attempt to add User to DB:
    try {
      User newUser = masterUserRepository.save(user);
      logger.info("User {} added to DB", newUser.getUsername());
      return newUser;

    } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
      // Handle exceptions cause by incompatible data formats (either java or sql ddl mismatch in definitions):
      logger.error("Unable to register User {} in DB, Reason: {}", user.getUsername(), e.getMessage());
      throw new DataIntegrityViolationException("Invalid User provided. Incompatible with database!");

    } catch (PersistenceException e) {
      // Handle other exceptions relating to persistence and connectivity:
      logger.error("Persistence exception occurred while registering User {}, Reason: {}", user.getUsername(), e.getMessage());
      throw new PersistenceException(e);
    }
  }


  @Override public User readUser(String username, String encryptedPassword) throws NotFoundInDBException, DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    if(username == null)
      throw new DataIntegrityViolationException("Username cannot be null");

    // Attempt to fetch User from DB:
    try {
      // Causes the repository to query the database. If no match is found, an error is thrown immediately.
      List<User> usersFound = masterUserRepository.findByUsernameAndEncryptedPassword(username, encryptedPassword);
      if(usersFound.isEmpty())
        throw new NotFoundInDBException("User {" + username + "} not found in DB");
      else if (usersFound.size() > 1)
        throw new DataIntegrityViolationException("Multiple matching users found in database. Only one matching user is allowed");

      logger.info("User {} read from DB", username);
      return usersFound.getFirst();

    } catch (IllegalArgumentException | ConstraintViolationException | DataIntegrityViolationException e) {
      // Handle exceptions cause by incompatible data formats (either java or sql ddl mismatch in definitions):
      logger.error("Unable to fetch User {} in DB, Reason: {}", username, e.getMessage());
      throw new DataIntegrityViolationException("Invalid Username provided. Incompatible with database!");

    } catch (PersistenceException e) {
      // Handle other exceptions relating to persistence and connectivity:
      logger.error("Persistence exception occurred while fetching User {}, Reason: {}", username, e.getMessage());
      throw new PersistenceException(e);
    }
  }
}
