package dk.sep3.dbserver.grpc.adapters.java_to_grpc;

import dk.sep3.dbserver.model.passwordManager.db_entities.User;
import dk.sep3.dbserver.grpc.factories.UserGrpcFactory;
import grpc.UserData;

/** <p>Responsible for converting java database server compatible db entities into gRPC connection data entries</p> */
public class UserToGrpcUserData
{
  /** <p>Converts the database entity 'User' into a gRPC compatible data type 'UserData', ready for transmission through gRPC connection.</p>
   * @param userEntity a database compatible Entity type.
   * @return a gRPC compatible data type. */
  public static UserData convertToGrpcUserData(User userEntity) {
    if (userEntity == null)
      return null;

    // Build and return the database server compatible User entity:
    return UserGrpcFactory.buildGrpcUserData(userEntity.getId(), userEntity.getUsername(), userEntity.getEncryptedPassword());
  }
}
