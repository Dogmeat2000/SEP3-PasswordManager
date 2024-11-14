package dk.sep3.dbserver.grpc.adapters.commands.implementations;

import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommand;
import dk.sep3.dbserver.grpc.adapters.grpc_to_java.LoginEntryDTOtoLoginEntryEntity;
import dk.sep3.dbserver.grpc.adapters.grpc_to_java.MasterUserDTOtoMasterUserEntity;
import dk.sep3.dbserver.grpc.factories.GenericResponseFactory;
import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import dk.sep3.dbserver.service.Pm.LoginEntryRepositoryService;
import grpc.GenericRequest;
import grpc.GenericResponse;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class CreateLoginEntryCommand implements GrpcCommand {

    private final LoginEntryRepositoryService loginEntryServiceImpl;

    @Autowired
    public CreateLoginEntryCommand(LoginEntryRepositoryService loginEntryServiceImpl) {
        this.loginEntryServiceImpl = loginEntryServiceImpl;
    }

    @Override public GenericResponse execute(GenericRequest request) throws DataIntegrityViolationException, PersistenceException {
        // Identify what type of DTO to convert to java compatible format:
        if(!request.getDataCase().equals(GenericRequest.DataCase.LOGINENTRY))
            throw new DataIntegrityViolationException("Argument is not valid.");

        System.out.println(request.getLoginEntry().toString());
        // Convert to db compatible entity:
        LoginEntry loginEntry = LoginEntryDTOtoLoginEntryEntity.convertToLoginEntryEntity(request.getLoginEntry());

        System.out.println(loginEntry.toString());

        // Execute the proper action:
        loginEntry = loginEntryServiceImpl.createLoginEntry(loginEntry);

        // Translate the response returned from the DB into a gRPC compatible type, before sending back to the client:
        return GenericResponseFactory.buildGrpcGenericResponseWithLoginEntryDTO(201, loginEntry);
    }
}
