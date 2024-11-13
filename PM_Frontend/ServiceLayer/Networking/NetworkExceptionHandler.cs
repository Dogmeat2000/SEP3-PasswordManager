using System.Data;
using System.Diagnostics;
using System.Net;
using System.Text.Json;

namespace ServiceLayer.Networking;

/**<summary>
 * Is responsible for receiving and translating http StatusCodes into C# exceptions,
 * where the StatusCodes correspond to any non-success code.
 * </summary> */
public class NetworkExceptionHandler {
    /**<summary>Receives http statusCodes and translates these into proper C# exceptions, causing the proper exception to be thrown in the application.
     * This allows for other C# classes to properly act upon these exceptions. This method ignores any success codes.
     * </summary>
     * <param name="statusCode">The http statusCode for the error.</param>
     * <param name="exceptionMsg">The http exception message / reason, typically provided alongside the status code.</param>
     * <exception cref="ArgumentException">When statusCode 400 'BadRequest' is received. </exception>
     * <exception cref="KeyNotFoundException">When statusCode 404 'NotFound' is received. </exception>
     * <exception cref="UnauthorizedAccessException">When statusCode 405 'MethodNotAllowed' is received. </exception>
     * <exception cref="DataException">When statusCode 409 'Conflict' is received. </exception>
     * <exception cref="IOException">When statusCode 500 'InternalServerError' is received. </exception>
     * <exception cref="UnreachableException">When statusCode 503 'ServiceUnavailable' is received. </exception>
     * <exception cref="Exception">When any statusCode other than the above specified, is received (excluding success codes). </exception>
     */
    public static void HandleException(HttpStatusCode statusCode, string? exceptionMsg) {
        // Extract message from the provided exception message, if not a successCode:
        
        string? errorMsg = null;
        if (((int)statusCode < 200 || (int)statusCode >= 300) && exceptionMsg != null) {
            using JsonDocument msg = JsonDocument.Parse(exceptionMsg);
            errorMsg = msg.RootElement.GetProperty("message").GetString();
        }
        
        switch ((int) statusCode) {
            case >= 200 and < 300: // Success code. Throw nothing.
                return;
            
            case 400: // BadRequest. Provided arguments are invalid, etc.
                throw new ArgumentException(errorMsg ?? exceptionMsg);
            
            case 404: // NotFound. The desired data could not be located in the repo.
                throw new KeyNotFoundException(errorMsg ?? exceptionMsg);
            
            case 405: // MethodNotAllowed. Client does not have permission to perform this command.
                throw new UnauthorizedAccessException(errorMsg ?? exceptionMsg);
            
            case 409: // Conflict. I.e. trying to register a MasterUser that already exists.
                throw new DataException(errorMsg ?? exceptionMsg);
            
            case >= 400 and < 500: // Catch other statusCode 400's:
                throw new Exception(errorMsg ?? exceptionMsg);
            
            case 500: //InternalServerError
                throw new IOException(errorMsg ?? exceptionMsg);
            
            case 503: //ServiceUnavailable, i.e. servers are online but services are offline.
                throw new UnreachableException(errorMsg ?? exceptionMsg);
            
            case >= 500 and < 512: // Catch other statusCode 500's:
                throw new Exception(errorMsg ?? exceptionMsg);
            
            default:
                throw new Exception(errorMsg ?? exceptionMsg);
        }
    }
}