using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Networking;

/**
 * Handles the communication with the Web-api, sends and receives requests.
 */
public interface IWebApiClient
{
    Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto);

    Task<ServerResponse> ReadMasterUserAsync(int masterUserId);
}