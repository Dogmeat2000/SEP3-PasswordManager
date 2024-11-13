using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Networking;

/**
 * Handles the communication with the Web-api, sends and receives requests.
 */
public interface IWebApiClient
{
    // MasterUser methods:
    Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto);
    Task<ServerResponse> ReadMasterUserAsync(int masterUserId);
    
    // LoginEntry methods:
    Task<ServerResponse> CreateLoginEntryAsync(LoginEntryDTO encryptLoginEntryAsync);
    
    /**<summary>Reads all login entries from the database.</summary>
     * <returns>A ServerResponse object containing a DTO of LoginEntryListDTO type.</returns>*/
    Task<ServerResponse> ReadLoginEntriesAsync();
    Task<ServerResponse> UpdateLoginEntryAsync(LoginEntryDTO encryptLoginEntryAsync);
    Task<ServerResponse> DeleteLoginEntryAsync(LoginEntryDTO entryToDelete);
}