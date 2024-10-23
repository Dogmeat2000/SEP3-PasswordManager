package dk.sep3.dbserver.grpc.factories;

import dk.sep3.dbserver.model.passwordManager.db_entities.User;
import grpc.GenericResponse;

public class GenericResponseFactory
{
  public static GenericResponse buildGrpcGenericResponseWithMasterUserDTO(int statusCode, User masterUser) {
    return GenericResponse.newBuilder()
        .setStatusCode(statusCode)
        .setMasterUser(MasterUserDTOGrpcFactory.buildGrpcMasterUserDTO(masterUser.getId(), masterUser.getUsername(), masterUser.getEncryptedPassword()))
        .build();
  }

  public static GenericResponse buildGrpcGenericResponseWithError(int statusCode) {
    return GenericResponse.newBuilder()
        .setStatusCode(statusCode)
        .build();
  }
}
