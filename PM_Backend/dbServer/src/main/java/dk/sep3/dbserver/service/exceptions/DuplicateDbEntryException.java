package dk.sep3.dbserver.service.exceptions;

public class DuplicateDbEntryException extends RuntimeException
{
  public DuplicateDbEntryException(String message) {
    super(message);
  }
}
