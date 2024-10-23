package dk.sep3.dbserver.grpc.adapters.commands;

import grpc.GenericRequest;
import grpc.GenericResponse;

/** <p>Generic Command Pattern interface, that defines the parent interface that all gRPC Commands must implement.</p>*/
public interface GrpcCommand
{
  /** <p></p>Executes the database server logic associated with this command implementation</p>
   * @param request A gRPC compatible request containing the necessary information for the command implementation to properly execute this command.
   * @return A gRPC compatible response, ready for transmission back to the gRPC client.
   * @throws IllegalArgumentException If the contents of the gRPC request are not valid in the context of this command execution. */
  GenericResponse execute(GenericRequest request) throws IllegalArgumentException;
}
