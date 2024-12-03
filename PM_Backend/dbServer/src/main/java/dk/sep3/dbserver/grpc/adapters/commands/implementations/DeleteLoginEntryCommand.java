package dk.sep3.dbserver.grpc.adapters.commands.implementations;

import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommand;
import dk.sep3.dbserver.grpc.adapters.grpc_to_java.LoginEntryDTOtoLoginEntryEntity;
import dk.sep3.dbserver.grpc.factories.GenericResponseFactory;
import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import dk.sep3.dbserver.service.Pm.LoginEntryRepositoryService;
import grpc.GenericRequest;
import grpc.GenericResponse;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/** <p>Defines the database server logic relating to executing this command</p>*/
@Component
public class DeleteLoginEntryCommand implements GrpcCommand {
    private final LoginEntryRepositoryService loginEntryServiceImpl;

    @Autowired
    public DeleteLoginEntryCommand(LoginEntryRepositoryService loginEntryServiceImpl) {
        this.loginEntryServiceImpl = loginEntryServiceImpl;
    }

    @Override
    public GenericResponse execute(GenericRequest request) throws DataIntegrityViolationException, PersistenceException {

        // Convert to db compatible entity:
        LoginEntry loginEntry = LoginEntryDTOtoLoginEntryEntity.convertToLoginEntryEntity(request.getLoginEntry());

        // Execute the proper action:
        loginEntryServiceImpl.deleteLoginEntry(loginEntry);

        // Translate the response returned from the DB into a gRPC compatible type, before sending back to the client:
        return GenericResponseFactory.buildGrpcGenericResponseEmptyDTO(200, "entry is deleted");
    }
}
