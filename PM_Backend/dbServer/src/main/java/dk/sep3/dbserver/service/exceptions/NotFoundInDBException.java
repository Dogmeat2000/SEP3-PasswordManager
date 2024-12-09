package dk.sep3.dbserver.service.exceptions;

/**<p>Exception Class used to indicate that the requested information could not be found in the database/repository. </p>*/
public class NotFoundInDBException extends RuntimeException
{
  public NotFoundInDBException(String message) {
    super(message);
  }
}
