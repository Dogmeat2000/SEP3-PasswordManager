package dk.sep3.dbserver.service.exceptions;

/**<p>Exception Class used to indicate that the database/repository contains a duplicate entry already. </p>*/
public class DuplicateDbEntryException extends RuntimeException
{
  public DuplicateDbEntryException(String message) {
    super(message);
  }
}
