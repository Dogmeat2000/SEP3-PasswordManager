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

    Task<ServerResponse> ReadMasterUserAsync(MasterUserDTO masterUserDto);
    Task<ServerResponse> CreateLoginEntryAsync(LoginEntryDTO newEntry);
    
    /**<summary>Reads all login entries from the database, associated with the logged-in user.</summary>
     * <param name="dto">An encrypted <see cref="MasterUserDTO"/> object that contains the currently logged in MasterUser's id, username and password</param>
     * <returns> <p>If Successful: A <see cref="ServerResponse"/> object containing an <b>encrypted</b> DTO of <see cref="LoginEntryListDTO"/> type, associated with the logged-in user...</p>
     * <p>If unsuccessful: A <see cref="ServerResponse"/> object containing the received HTTP status code and exception message</p></returns>*/
    Task<ServerResponse> ReadLoginEntriesAsync(MasterUserDTO dto);
    Task<ServerResponse> UpdateLoginEntryAsync(LoginEntryDTO encryptLoginEntryAsync);
    Task<ServerResponse> DeleteLoginEntryAsync(LoginEntryDTO entryToDelete);
}