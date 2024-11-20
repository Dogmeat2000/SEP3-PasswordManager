package dk.sep3.dbserver.grpc.adapters.commands.implementations;

import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommand;
import dk.sep3.dbserver.grpc.adapters.grpc_to_java.MasterUserDTOtoMasterUserEntity;
import dk.sep3.dbserver.grpc.factories.GenericResponseFactory;
import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import dk.sep3.dbserver.service.Pm.LoginEntryRepositoryService;
import dk.sep3.dbserver.service.Pm.MasterUserRepositoryService;
import grpc.GenericRequest;
import grpc.GenericResponse;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

/** <p>Defines the database server logic relating to executing this command</p>*/
@Component
public class ReadLoginEntriesCommand implements GrpcCommand
{
  private final LoginEntryRepositoryService loginEntryRepositoryServiceImpl;
  private final MasterUserRepositoryService masterUserServiceImpl;

  @Autowired
  public ReadLoginEntriesCommand(LoginEntryRepositoryService loginEntryRepositoryServiceImpl,
      MasterUserRepositoryService masterUserServiceImpl){

    this.loginEntryRepositoryServiceImpl = loginEntryRepositoryServiceImpl;
    this.masterUserServiceImpl = masterUserServiceImpl;
  }

  @Override public GenericResponse execute(GenericRequest request) throws DataIntegrityViolationException, PersistenceException {
    // Identify what type of DTO to convert to java compatible format:
    if(!request.getDataCase().equals(GenericRequest.DataCase.MASTERUSER))
      throw new DataIntegrityViolationException("Wrong DTO provided");

    // Convert to db compatible entity:
    MasterUser masterUser = MasterUserDTOtoMasterUserEntity.convertToMasterUserEntity(request.getMasterUser());

    // Validate the MasterUser:
    if(masterUser == null
        || masterUser.getMasterUsername() == null
        || masterUser.getEncryptedPassword() == null)
      throw new DataIntegrityViolationException("Invalid DTO provided");

    // Check if the MasterUser is a valid account:
    MasterUser foundMasterUser = null;
    try {
      // Will throw an exception if the user cannot be found in the db.
      foundMasterUser = masterUserServiceImpl.readMasterUser(masterUser.getMasterUsername(), masterUser.getEncryptedPassword());

      if(foundMasterUser == null)
        throw new DataIntegrityViolationException("Invalid DTO provided");
    } catch (Exception e) {
      throw new DataIntegrityViolationException("Cannot look up loginEntries for a MasterUser that does not exist in the repository");
    }

    // Execute the proper action:
    List<LoginEntry> loginEntries = loginEntryRepositoryServiceImpl.readLoginEntriesByMasterUserId(foundMasterUser.getId());

    // Translate the response returned from the DB into a gRPC compatible type, before sending back to the client:
    return GenericResponseFactory.buildGrpcGenericResponseWithLoginEntryListDTO(200, loginEntries);
  }
}
