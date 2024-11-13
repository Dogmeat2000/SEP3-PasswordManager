package dk.sep3.dbserver.grpc.adapters.java_to_grpc;

import dk.sep3.dbserver.grpc.factories.MasterUserDTOGrpcFactory;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import grpc.MasterUserDTO;

/** <p>Responsible for converting java database server compatible db entities into gRPC connection data entries</p> */
public class MasterUsertoGrpcMasterUserDTO
{
  /** <p>Converts the gRPC compatible data type 'MasterUserDTO' into the database server compatible 'MasterUser' entity, ready for interaction with the database.</p>
   * @param masterUserEntity a gRPC data type.
   * @return a database (db server) compatible entity */
  public static MasterUserDTO convertToGrpc(MasterUser masterUserEntity) {
    if (masterUserEntity == null)
      return null;

    // Build and return the database server compatible User entity:
    return MasterUserDTOGrpcFactory.buildGrpcMasterUserDTO(masterUserEntity.getId(), masterUserEntity.getMasterUsername(), masterUserEntity.getEncryptedPassword());
  }
}
