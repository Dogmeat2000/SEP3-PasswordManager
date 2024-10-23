package dk.sep3.dbserver.grpc.adapters.java_to_grpc;

import dk.sep3.dbserver.grpc.factories.UserGrpcFactory;
import dk.sep3.dbserver.model.passwordManager.db_entities.User;
import grpc.MasterUserDTO;

/** <p>Responsible for converting java database server compatible db entities into gRPC connection data entries</p> */
public class MasterUsertoGrpcMasterUserDTO
{
  /** <p>Converts the database entity 'User' into a gRPC compatible data type 'UserData', ready for transmission through gRPC connection.</p>
   * @param userEntity a database compatible Entity type.
   * @return a gRPC compatible data type. */
  public static MasterUserDTO convertToGrpc(User masterUserEntity) {
    if (masterUserEntity == null)
      return null;

    // Build and return the database server compatible User entity:
    return UserGrpcFactory.buildGrpcUserData(masterUserEntity.getId(), masterUserEntity.getUsername(), masterUserEntity.getEncryptedPassword());
  }
}
