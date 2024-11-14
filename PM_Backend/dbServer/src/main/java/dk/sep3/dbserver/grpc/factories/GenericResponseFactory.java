package dk.sep3.dbserver.grpc.factories;

import dk.sep3.dbserver.grpc.adapters.java_to_grpc.LoginEntrytoGrpcLoginEntry;
import dk.sep3.dbserver.grpc.adapters.java_to_grpc.MasterUsertoGrpcMasterUserDTO;
import dk.sep3.dbserver.model.Pm.db_entities.LoginEntry;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import grpc.GenericResponse;
import grpc.LoginEntryListDTO;

import java.util.List;

public class GenericResponseFactory
{
  public static GenericResponse buildGrpcGenericResponseWithMasterUserDTO(int statusCode, MasterUser masterUser) {
    return GenericResponse.newBuilder()
        .setStatusCode(statusCode)
        .setMasterUser(MasterUsertoGrpcMasterUserDTO.convertToGrpc(masterUser))
        .build();
  }


  public static GenericResponse buildGrpcGenericResponseWithError(int statusCode, String exceptionMsg) {
    return GenericResponse.newBuilder()
        .setStatusCode(statusCode)
        .setException(grpc.Exception.newBuilder().setException(exceptionMsg))
        .build();
  }

  public static GenericResponse buildGrpcGenericResponseWithLoginEntryDTO(int statusCode, LoginEntry loginEntry)
  {
    return GenericResponse.newBuilder()
            .setStatusCode(statusCode)
            .setLoginEntry(LoginEntrytoGrpcLoginEntry.convertToGrpc(loginEntry))
            .build();
  }


  public static GenericResponse buildGrpcGenericResponseWithLoginEntryListDTO(int statusCode, List<LoginEntry> list) {
    GenericResponse.Builder responseBuilder = GenericResponse.newBuilder()
        .setStatusCode(statusCode);

    // Convert all the LoginEntries:
    LoginEntryListDTO.Builder loginEntryListDTOBuilder = LoginEntryListDTO.newBuilder();
    for (LoginEntry loginEntry : list)
      loginEntryListDTOBuilder.addLoginEntries(LoginEntrytoGrpcLoginEntry.convertToGrpc(loginEntry));

    // Add to main grpc response:
    responseBuilder.setLoginEntries(loginEntryListDTOBuilder.build());

    return responseBuilder.build();
  }

  public static GenericResponse buildGrpcGenericResponseEmptyDTO(int statusCode, String message) {
    return GenericResponse.newBuilder()
            .setStatusCode(statusCode)
            .build();
  }
}
