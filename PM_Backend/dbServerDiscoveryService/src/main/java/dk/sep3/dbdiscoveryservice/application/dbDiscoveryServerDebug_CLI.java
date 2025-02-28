package dk.sep3.dbdiscoveryservice.application;

import dk.sep3.dbserver.grpc.adapters.grpc_to_java.MasterUserDTOtoMasterUserEntity;
import dk.sep3.dbserver.grpc.factories.MasterUserDTOGrpcFactory;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.MasterUserDTO;
import grpc.PasswordManagerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;

/** <p>This is a simple CommandLineInterface debugging tool, to test if the Discovery Service properly retransmits requests to a gRPC server.</p>*/
public class dbDiscoveryServerDebug_CLI
{
  private static final String host = "localhost";
  private static final int port = 8090;

  public static void main(String[] args) {
    System.out.println("\nSimple DEBUGGER: That can be used to manually test the functionality of the Database Discovery Server!");

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
            MasterUser newMasterUser = MasterUserDTOtoMasterUserEntity.convertToMasterUserEntity(createdMasterUser.getMasterUser());
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
          masterPassword = input.nextLine();
          System.out.println("Fetching masterUser {" + masterUsername + ", " + masterPassword + "}");
          try {
            ManagedChannel channel = channel();
            PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub stub = PasswordManagerServiceGrpc.newBlockingStub(channel);

            MasterUserDTO masterUserDTO = MasterUserDTOGrpcFactory.buildGrpcMasterUserDTO(0, masterUsername, masterPassword);
            GenericRequest request = buildMasterUserRequest("ReadMasterUser", masterUserDTO);

            GenericResponse readMasterUser = stub.handleRequest(request);

            MasterUser newMasterUser = MasterUserDTOtoMasterUserEntity.convertToMasterUserEntity(readMasterUser.getMasterUser());
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

  protected static GenericRequest buildMasterUserRequest(String requestType, MasterUserDTO masterUserDTO) {
    return GenericRequest.newBuilder()
        .setRequestType(requestType)
        .setMasterUser(masterUserDTO)
        .build();
  }
}
