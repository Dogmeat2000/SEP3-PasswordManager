package dk.sep3.dbserver.grpc.adapters.commands.implementations;

import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommand;
import dk.sep3.dbserver.grpc.adapters.grpc_to_java.MasterUserDTOtoMasterUserEntity;
import dk.sep3.dbserver.grpc.factories.GenericResponseFactory;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import dk.sep3.dbserver.service.Pm.MasterUserRepositoryService;
import grpc.GenericRequest;
import grpc.GenericResponse;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/** Defines the database server logic relating to executing this command*/
@Component
public class ReadMasterUserCommand implements GrpcCommand
{
  private final MasterUserRepositoryService masterUserServiceImpl;

  @Autowired
  public ReadMasterUserCommand(MasterUserRepositoryService masterUserServiceImpl){
    this.masterUserServiceImpl = masterUserServiceImpl;
  }

  @Override public GenericResponse execute(GenericRequest request) throws PersistenceException, DataIntegrityViolationException, NotFoundInDBException {
    // Identify what type of DTO to convert to java compatible format:
    if(!request.getDataCase().equals(GenericRequest.DataCase.MASTERUSER))
      throw new DataIntegrityViolationException("Argument is not valid.");

    // Convert to db compatible entity:
    MasterUser masterUser = MasterUserDTOtoMasterUserEntity.convertToMasterUserEntity(request.getMasterUser());

    // Execute the proper action:
    masterUser = masterUserServiceImpl.readMasterUser(masterUser.getMasterUsername(), masterUser.getEncryptedPassword());
    System.out.println("\n\n\n" + masterUser + "\n\n\n");

    // Translate the response returned from the DB into a gRPC compatible type, before sending back to the client:
    return GenericResponseFactory.buildGrpcGenericResponseWithMasterUserDTO(200, masterUser);
  }
}
