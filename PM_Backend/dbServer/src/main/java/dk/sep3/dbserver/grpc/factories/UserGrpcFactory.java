package dk.sep3.dbserver.grpc.factories;

import grpc.UserData;
import grpc.UserNameAndPswd;

// TODO: DELETE CLASS!

/**
 * <p>Provides static methods for building gRPC compatible versions of the User entity.<br>
 * Is used when exchanging gRPC calls/data between the load balancer and the database server</p>
 * */
public class UserGrpcFactory
{
  /** <p>Builds a gRPC compatible 'UserData', which simply is a gRPC compatible version of the User entity.</p>
   * @param username A String containing the username associated with this user.
   * @param encryptedPassword A String containing the password associated with this user.
   * @return A gRPC compatible data type. */
  public static UserData buildGrpcUserData(int id, String username, String encryptedPassword) {
    return UserData.newBuilder()
        .setId(id)
        .setUsername(username)
        .setEncryptedPassword(encryptedPassword)
        .build();
  }

  
  /** <p>Builds a gRPC compatible 'UserName', which simply is a gRPC compatible version of the User's name variable.</p>
   * @param username A String containing the username associated with this user.
   * @param encryptedPassword A String containing the password associated with this user.
   * @return A gRPC compatible data type. */
  public static UserNameAndPswd buildGrpcUserNameAndPswd(String username, String encryptedPassword) {
    return UserNameAndPswd.newBuilder()
        .setUsername(username)
        .setEncryptedPassword(encryptedPassword)
        .build();
  }
}
