package dk.sep3.dbserver.service.exceptions;

/**<p>Exception Class used to indicate that the received gRPC request contains a unrecognized/invalid GrpcCommand. </p>*/
public class IllegalGrpcCommand extends RuntimeException
{
  public IllegalGrpcCommand(String message) {
    super(message);
  }
}
