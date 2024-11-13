package dk.sep3.dbserver.grpc.adapters.grpc_to_java;

import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import grpc.LoginEntryDTO;

public class LoginEntryDTOtoLoginEntryEntity {

    public static LoginEntry convertToLoginEntryEntity(LoginEntryDTO grpcDTO)
    {
        if (grpcDTO == null) {
            return null;
        }

        int id = grpcDTO.getId();
        int masterUserId = grpcDTO.getMasterUserId();
        String entryName = grpcDTO.getEntryName();
        String entryUsername = grpcDTO.getEntryUsername();
        String entryPassword = grpcDTO.getEntryPassword();
        String entryAddress = grpcDTO.getEntryAddress();
        String entryCategory = grpcDTO.getCategory(); //TOdo convert category to correct category id

        return new LoginEntry(id, entryUsername, entryPassword, entryName, entryAddress, 1, masterUserId);
    }
}
