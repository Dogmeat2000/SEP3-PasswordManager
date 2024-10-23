package dk.sep3.dbserver.application;

import dk.sep3.dbserver.grpc.adapters.grpc_to_java.MasterUserDTOtoMasterUserEntity;
import dk.sep3.dbserver.model.passwordManager.db_entities.User;
import dk.sep3.dbserver.grpc.factories.UserGrpcFactory;
import grpc.UserData;
import grpc.UserNameAndPswd;
import grpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;

// This is a simple CommandLineInterface debugging tool, to test the foundational DB Server architecture (simulate gRPC calls and if they properly affect the database repository).
public class dbServerDebug_CLI
{
  private static final String host = "localhost";
  private static final int port = 9090;

  public static void main(String[] args) {
    System.out.println("\nSimple DEBUGGER: That can be used to manually test the basic architecture of the DB Server!");

    Scanner input = new Scanner(System.in);

    while(true) {
      System.out.println("\nAvailable database server commands: 'AddUser', 'ViewOneUser'");
      System.out.print(": ");
      String cmd = input.nextLine();

      String username;
      String password;

      switch(cmd.toLowerCase()) {
        case "adduser":
          System.out.println("Register User in DB selected!");
          System.out.print("Enter username: ");
          username = input.nextLine();
          System.out.print("Enter encrypted password hash: ");
          password = input.nextLine();
          System.out.println("Creating user {" + username + ", " + password + "}");
          try {
            ManagedChannel channel = channel();
            UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
            UserData data = UserGrpcFactory.buildGrpcUserData(-1, username, password);
            UserData createdUser = stub.registerUser(data);
            User newUser = MasterUserDTOtoMasterUserEntity.convertToUserEntity(createdUser);
            System.out.println("Created user in DB: " + newUser.toString());
          } catch (StatusRuntimeException e) {
            System.out.println("Error: " + e.getStatus().getDescription());
          } finally {
            channel().shutdown();
          }
          break;
        case "viewoneuser":
          System.out.println("Fetch User from DB selected!");
          System.out.print("Enter username: ");
          username = input.nextLine();
          System.out.print("Enter encrypted password hash: ");
          password = input.nextLine();
          System.out.println("Fetching user {" + username + ", " + password + "}");
          try {
            ManagedChannel channel = channel();
            UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
            UserNameAndPswd data = UserGrpcFactory.buildGrpcUserNameAndPswd(username, password);
            UserData foundUser = stub.getUser(data);
            User newUser = MasterUserDTOtoMasterUserEntity.convertToUserEntity(foundUser);
            System.out.println("Found user in DB: " + newUser.toString());
          } catch (StatusRuntimeException e) {
            System.out.println("Error: " + e.getStatus().getDescription());
          } finally {
            channel().shutdown();
          }
          break;
        default:
          System.out.println("Invalid input!\n");
      }
    }

  }

  protected static ManagedChannel channel() {
    return ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext()
        .build();
  }
}
