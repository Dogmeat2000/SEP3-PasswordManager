package dk.sep3.dbserver.grpc.factories;

import dk.sep3.dbserver.grpc.adapters.java_to_grpc.MasterUsertoGrpcMasterUserDTO;
import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;
import grpc.GenericResponse;

public class GenericResponseFactory
{
  public static GenericResponse buildGrpcGenericResponseWithMasterUserDTO(int statusCode, MasterUser masterUser) {
    return GenericResponse.newBuilder()
        .setStatusCode(statusCode)
        .setMasterUser(MasterUsertoGrpcMasterUserDTO.convertToGrpc(masterUser))
        .build();
  }

  public static GenericResponse buildGrpcGenericResponseWithError(int statusCode) {
    return GenericResponse.newBuilder()
        .setStatusCode(statusCode)
        .build();
  }
}
