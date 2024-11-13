package dk.sep3.dbserver.grpc.factories;

import grpc.LoginEntryDTO;

public class LoginEntryDTOGrpcFactory {
    public static LoginEntryDTO buildGrpcLoginEntryDTO(int id, String entryName, String entryUsername, String entryPassword, String entryAddress, String category, int masterUserId)
    {
        return LoginEntryDTO.newBuilder()
                .setId(id)
                .setEntryName(entryName)
                .setEntryUsername(entryUsername)
                .setEntryPassword(entryPassword)
                .setEntryAddress(entryAddress)
                .setCategory(category)
                .setMasterUserId(masterUserId)
                .build();
    }
}
