package dk.sep3.dbserver.service.Pm;

import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;

public interface LoginEntryRepositoryService {
    LoginEntry createLoginEntry(LoginEntry loginEntry) throws DataIntegrityViolationException, PersistenceException;
}
