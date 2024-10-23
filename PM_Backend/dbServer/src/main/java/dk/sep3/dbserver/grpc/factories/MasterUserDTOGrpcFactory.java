package dk.sep3.dbserver.grpc.factories;

import grpc.MasterUserDTO;

public class MasterUserDTOGrpcFactory
{
  public static MasterUserDTO buildGrpcMasterUserDTO(int id, String masterUsername, String masterPassword) {
    return MasterUserDTO.newBuilder()
        .setId(id)
        .setMasterUsername(masterUsername)
        .setMasterPassword(masterPassword)
        .build();
  }
}
