package dk.sep3.dbserver.service.passwordManager;

import dk.sep3.dbserver.model.passwordManager.db_entities.User;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

public class PasswordManagerServiceImpl
{
  @Override public User registerUser(User user) throws DataIntegrityViolationException, PersistenceException {
    // Validate received data, before passing to repository/database:
    if(user == null || user.getUsername() == null || user.getEncryptedPassword() == null)
      throw new DataIntegrityViolationException("User cannot be null");

    // Attempt to add User to DB:
    try {
      User newUser = userRepository.save(user);
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
}
