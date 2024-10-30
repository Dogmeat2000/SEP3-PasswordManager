using Shared.Dtos;

namespace ServiceLayer.Networking;

/**
 * Handles the communication with the Web-api, sends and receives requests.
 */
public interface IWebApiClient
{
    Task<MasterUserDTO> CreateMasterUserAsync(MasterUserDTO masterUserDto);

    Task<MasterUserDTO> ReadMasterUserAsync(int masterUserId);
}