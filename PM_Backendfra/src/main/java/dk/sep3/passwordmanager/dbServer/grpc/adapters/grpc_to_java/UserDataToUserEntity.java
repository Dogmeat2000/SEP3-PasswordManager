package dk.sep3.passwordmanager.dbServer.grpc.adapters.grpc_to_java;

import dk.sep3.passwordmanager.dbServer.db_entities.User;
import grpc.UserData;

/** <p>Responsible for converting a gRPC connection data entries into java database server compatible db entities</p> */
public class UserDataToUserEntity
{
  /** <p>Converts the gRPC compatible data type 'UserData' into the database server compatible 'User' entity, ready for interaction with the database.</p>
   * @param userData a gRPC data type.
   * @return a database (db server) compatible entity */
  public static User convertToUserEntity(UserData userData) {
    if (userData == null)
      return null;

    // Extract the User entity data from the gRPC data type:
    int id = userData.getId();
    String username = userData.getUsername();
    String encryptedPassword = userData.getEncryptedPassword();

    // Construct and return the database server compatible User entity:
    return new User(id, username, encryptedPassword);
  }
}
