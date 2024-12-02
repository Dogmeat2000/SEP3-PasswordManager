package dk.sep3.dbserver.grpc.adapters.java_to_grpc;

import dk.sep3.dbserver.grpc.factories.LoginEntryDTOGrpcFactory;
import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import grpc.LoginEntryDTO;

public class LoginEntrytoGrpcLoginEntry {
    public static LoginEntryDTO convertToGrpc(LoginEntry loginEntryEntity) {
        if (loginEntryEntity == null)
            return null;

        // Build and return the database server compatible User entity:
        return LoginEntryDTOGrpcFactory.buildGrpcLoginEntryDTO(loginEntryEntity.getId(), loginEntryEntity.getEntryName(), loginEntryEntity.getEntryUsername(), loginEntryEntity.getEntryPassword(), loginEntryEntity.getEntryAddress(), "Unspecified", loginEntryEntity.getMasterUserId());
    }
}
