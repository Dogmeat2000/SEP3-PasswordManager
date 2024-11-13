using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Networking;

/**
 * Handles the communication with the Web-api, sends and receives requests.
 */
public interface IWebApiClient
{
    // MasterUser methods:
    /**
     * Sends a request to create a new master user in the Web API.
     *
     * @param masterUserDto DTO containing the master user data to be created.
     * @return ServerResponse containing the result of the creation request.
     */
    Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto);

    /**
     * Sends a request to retrieve a master user by their ID from the Web API.
     *
     * @param masterUserId The ID of the master user to be retrieved.
     * @return ServerResponse containing the retrieved MasterUserDTO.
     */
    Task<ServerResponse> ReadMasterUserAsync(int masterUserId);
    Task<ServerResponse> CreateLoginEntryAsync(LoginEntryDTO encryptLoginEntryAsync);
    
    /**<summary>Reads all login entries from the database.</summary>
     * <returns>A ServerResponse object containing a DTO of LoginEntryListDTO type.</returns>*/
    Task<ServerResponse> ReadLoginEntriesAsync();
    Task<ServerResponse> UpdateLoginEntryAsync(LoginEntryDTO encryptLoginEntryAsync);
    Task<ServerResponse> DeleteLoginEntryAsync(LoginEntryDTO entryToDelete);

    // TODO: Marcus, commented out due to errors.
    /**
     * Sends a request to authenticate a master user.
     * The Web API validates the credentials and returns an authentication token if successful.
     *
     * @param masterUserDto DTO containing master user credentials for authentication.
     * @return ServerResponse containing authentication results, such as a JWT token.
     */
    //Task<ServerResponse> AuthenticateUserAsync(MasterUserDTO masterUserDto);
}