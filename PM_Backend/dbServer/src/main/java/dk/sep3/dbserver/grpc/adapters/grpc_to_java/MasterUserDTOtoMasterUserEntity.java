package dk.sep3.dbserver.grpc.adapters.grpc_to_java;

import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import grpc.MasterUserDTO;

/** <p>Responsible for converting a gRPC connection data entries into java database server compatible db entities</p> */
public class MasterUserDTOtoMasterUserEntity
{
  /** <p>Converts the gRPC compatible data type 'MasterUserDTO' into the database server compatible 'MasterUser' entity, ready for interaction with the database.</p>
   * @param grpcDTO a gRPC data type.
   * @return a database (db server) compatible entity */
  public static MasterUser convertToMasterUserEntity(MasterUserDTO grpcDTO) {
    if (grpcDTO == null)
      return null;

    // Extract the MasterUser entity data from the gRPC data type:
    int id = grpcDTO.getId();
    String masterUsername = grpcDTO.getMasterUsername();
    String masterPassword = grpcDTO.getMasterPassword();

    // Construct and return the database server compatible User entity:
    return new MasterUser(id, masterUsername, masterPassword);
  }
}
