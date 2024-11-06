package dk.sep3.dbserver.application;

import dk.sep3.dbserver.grpc.adapters.grpc_to_java.MasterUserDTOtoMasterUserEntity;
import dk.sep3.dbserver.grpc.factories.GenericResponseFactory;
import dk.sep3.dbserver.grpc.factories.MasterUserDTOGrpcFactory;
import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;

import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.MasterUserDTO;
import grpc.PasswordManagerServiceGrpc;
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
      String masterPassword;

      switch(cmd.toLowerCase()) {
        case "addmasteruser":
          System.out.println("Register MasterUser in DB selected!");
          System.out.print("Enter masterUsername: ");
          masterUsername = input.nextLine();
          System.out.print("Enter masterPassword hash: ");
          masterPassword = input.nextLine();
          System.out.println("Creating masterUser {" + masterUsername + ", " + masterPassword + "}");
          try {
            ManagedChannel channel = channel();
            PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub stub = PasswordManagerServiceGrpc.newBlockingStub(channel);

            MasterUserDTO masterUserDTO = MasterUserDTOGrpcFactory.buildGrpcMasterUserDTO(0, masterUsername, masterPassword);
            GenericRequest request = buildMasterUserRequest("CreateMasterUser", masterUserDTO);

            GenericResponse createdMasterUser = stub.handleRequest(request);
            if(!createdMasterUser.getException().getException().isEmpty()){
              System.out.println("Failed to create MasterUser. Code: '" + createdMasterUser.getStatusCode() + "' Cause: " + createdMasterUser.getException().getException());
            } else {
              MasterUser newMasterUser = MasterUserDTOtoMasterUserEntity.convertToMasterUserEntity(createdMasterUser.getMasterUser());
              System.out.println("Created masterUser in DB: " + newMasterUser.toString());
              System.out.println("StatusCode: " + createdMasterUser.getStatusCode());
            }

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
          masterPassword = input.nextLine();
          System.out.println("Fetching masterUser {" + masterUsername + ", " + masterPassword + "}");
          try {
            ManagedChannel channel = channel();
            PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub stub = PasswordManagerServiceGrpc.newBlockingStub(channel);

            MasterUserDTO masterUserDTO = MasterUserDTOGrpcFactory.buildGrpcMasterUserDTO(0, masterUsername, masterPassword);
            GenericRequest request = buildMasterUserRequest("ReadMasterUser", masterUserDTO);

            GenericResponse readMasterUser = stub.handleRequest(request);

            if(!readMasterUser.getException().getException().isEmpty()){
              System.out.println("Failed to read MasterUser. Code: '" + readMasterUser.getStatusCode() + "' Cause: " + readMasterUser.getException().getException());
            } else {
              MasterUser newMasterUser = MasterUserDTOtoMasterUserEntity.convertToMasterUserEntity(readMasterUser.getMasterUser());
              System.out.println("Found masterUser in DB: " + newMasterUser.toString());
              System.out.println("StatusCode: " + readMasterUser.getStatusCode());
            }

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

  protected static GenericRequest buildMasterUserRequest(String requestType, MasterUserDTO masterUserDTO) {
    return GenericRequest.newBuilder()
        .setRequestType(requestType)
        .setMasterUser(masterUserDTO)
        .build();
  }
}
