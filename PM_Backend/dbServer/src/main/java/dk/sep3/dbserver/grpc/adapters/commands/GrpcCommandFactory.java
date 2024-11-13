package dk.sep3.dbserver.grpc.adapters.commands;

import dk.sep3.dbserver.grpc.adapters.commands.implementations.CreateLoginEntryCommand;
import dk.sep3.dbserver.grpc.adapters.commands.implementations.CreateMasterUserCommand;
import dk.sep3.dbserver.grpc.adapters.commands.implementations.ReadMasterUserCommand;
import dk.sep3.dbserver.service.exceptions.IllegalGrpcCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** <p>Builds and manages the main list/overview of all available gRPC commands that the gRPC server can process. </p>*/
@Component
public class GrpcCommandFactory
{
  private final Map<String, GrpcCommand> grpcCommandMap = new HashMap<>();
  private final String genericErrorMsg = "Invalid GRPC command defined in DTO object from Client.)";

  @Autowired
  public GrpcCommandFactory(List<GrpcCommand> commands) {
    // Register every available command, by adding to the HashMap below:
    grpcCommandMap.put("createmasteruser", commands.stream().filter(c -> c instanceof CreateMasterUserCommand).findFirst().orElseThrow(() -> new IllegalGrpcCommand(genericErrorMsg)));
    grpcCommandMap.put("readmasteruser", commands.stream().filter(c -> c instanceof ReadMasterUserCommand).findFirst().orElseThrow(() -> new IllegalGrpcCommand(genericErrorMsg)));
    grpcCommandMap.put("createloginentry", commands.stream().filter(c -> c instanceof CreateLoginEntryCommand).findFirst().orElseThrow(() -> new IllegalGrpcCommand(genericErrorMsg)));
    // Add more commands below: IMPORTANT: Please add any command in ALL LOWERCASE!

    //..
  }

  /** <p>Fetches a defined gRPC command, with execution logic</p>
   * @param command a String value containing the name of the gRPC command to execute.
   * @return The corresponding gRPC command, including necessary execution logic.
   * @throws IllegalGrpcCommand If the declared command is invalid. */
  public GrpcCommand getCommand(String command) throws IllegalGrpcCommand {
    // See if command exists:
    if(grpcCommandMap.containsKey(command.toLowerCase()))
      return grpcCommandMap.get(command.toLowerCase());
    else
      throw new IllegalGrpcCommand("Invalid GRPC command. Does not exist in grpcCommandMap/List");
  }
}
