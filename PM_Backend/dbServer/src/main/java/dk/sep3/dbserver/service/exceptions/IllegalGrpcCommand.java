package dk.sep3.dbserver.service.exceptions;

public class IllegalGrpcCommand extends RuntimeException
{
  public IllegalGrpcCommand(String message) {
    super(message);
  }
}
