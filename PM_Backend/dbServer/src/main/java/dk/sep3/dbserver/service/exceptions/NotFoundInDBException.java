package dk.sep3.dbserver.service.exceptions;

public class NotFoundInDBException extends RuntimeException
{
  public NotFoundInDBException(String message) {
    super(message);
  }
}
