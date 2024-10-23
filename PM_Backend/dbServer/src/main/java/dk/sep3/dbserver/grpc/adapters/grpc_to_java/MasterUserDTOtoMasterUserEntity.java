package dk.sep3.dbserver.grpc.adapters.grpc_to_java;

import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;
import grpc.MasterUserDTO;

/** <p>Responsible for converting a gRPC connection data entries into java database server compatible db entities</p> */
public class MasterUserDTOtoMasterUserEntity
{
  /** <p>Converts the gRPC compatible data type 'UserData' into the database server compatible 'User' entity, ready for interaction with the database.</p>
   * @param userData a gRPC data type.
   * @return a database (db server) compatible entity */
  public static MasterUser convertToMasterUserEntity(MasterUserDTO userGrpcDTO) {
    if (userGrpcDTO == null)
      return null;

    // Extract the MasterUser entity data from the gRPC data type:
    int id = userGrpcDTO.getId();
    String masterUsername = userGrpcDTO.getMasterUsername();
    String masterPassword = userGrpcDTO.getMasterPassword();

    // Construct and return the database server compatible User entity:
    return new MasterUser(id, masterUsername, masterPassword);
  }
}
