package dk.sep3.dbserver.application;

import dk.sep3.dbserver.grpc.adapters.grpc_to_java.MasterUserDTOtoMasterUserEntity;
import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;
import dk.sep3.dbserver.grpc.factories.MasterUserGrpcFactory;
import grpc.MasterUserData;
import grpc.MasterUserNameAndPswd;

import grpc.MasterUserServiceGrpc;

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
      System.out.println("\nAvailable database server commands: 'AddMasterUser', 'ViewOneMasterUser'");
      System.out.print(": ");
      String cmd = input.nextLine();

      String masterUsername;
      String password;

      switch(cmd.toLowerCase()) {
        case "addmasteruser":
          System.out.println("Register MasterUser in DB selected!");
          System.out.print("Enter masterUsername: ");
          masterUsername = input.nextLine();
          System.out.print("Enter encrypted password hash: ");
          password = input.nextLine();
          System.out.println("Creating masterUser {" + masterUsername + ", " + password + "}");
          try {
            ManagedChannel channel = channel();
            MasterUserServiceGrpc.MasterUserServiceBlockingStub stub = MasterUserServiceGrpc.newBlockingStub(channel);
            MasterUserData data = MasterUserGrpcFactory.buildGrpcMasterUserData(-1, masterUsername, password);
            MasterUserData createdMasterUser = stub.registerMasterUser(data);
            MasterUser newMasterUser = MasterUserDTOtoMasterUserEntity.convertToMasterUserEntity(createdMasterUser);
            System.out.println("Created masterUser in DB: " + newMasterUser.toString());
          } catch (StatusRuntimeException e) {
            System.out.println("Error: " + e.getStatus().getDescription());
          } finally {
            channel().shutdown();
          }
          break;
        case "viewonemasteruser":
          System.out.println("Fetch MasterUser from DB selected!");
          System.out.print("Enter masterUsername: ");
          masterUsername = input.nextLine();
          System.out.print("Enter encrypted password hash: ");
          password = input.nextLine();
          System.out.println("Fetching masterUser {" + masterUsername + ", " + password + "}");
          try {
            ManagedChannel channel = channel();
            MasterUserServiceGrpc.MasterUserServiceBlockingStub stub = MasterUserServiceGrpc.newBlockingStub(channel);
            MasterUserNameAndPswd data = MasterUserGrpcFactory.buildGrpcMasterUserNameAndPswd(masterUsername, password);
            MasterUserData foundMasterUser = stub.getMasterUser(data);
            MasterUser newMasterUser = MasterUserDTOtoMasterUserEntity.convertToMasterUserEntity(foundMasterUser);
            System.out.println("Found masterUser in DB: " + newMasterUser.toString());
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
