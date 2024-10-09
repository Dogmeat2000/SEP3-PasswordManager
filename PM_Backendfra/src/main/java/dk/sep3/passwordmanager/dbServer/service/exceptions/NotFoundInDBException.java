package dk.sep3.passwordmanager.dbServer.service.exceptions;

public class NotFoundInDBException extends RuntimeException
{
  public NotFoundInDBException(String message) {
    super(message);
  }
}
