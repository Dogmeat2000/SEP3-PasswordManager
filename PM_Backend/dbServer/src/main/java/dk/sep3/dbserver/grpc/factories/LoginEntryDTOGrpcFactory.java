package dk.sep3.dbserver.grpc.factories;

import grpc.LoginEntryDTO;

/** <p>Class is responsible for defining methods to build proper gRPC LoginEntryDto related messages.</p>*/
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
