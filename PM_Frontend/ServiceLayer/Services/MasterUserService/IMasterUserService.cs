using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.MasterUserService;

/**
 * Handles business logic for the MasterUser data entity
 * Business logic contains encryption, validation and so on
 */
public interface IMasterUserService
{
 /**
     * Sends the create-request for the master user further down
     * Handles validation before encryption
     * Encrypts the DTO before it is sent on
     * Decrypts the returning master user before it is returned.
     */
 Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto);

 /**
     * Sends the read-request for the master user further down
     * Handles eventual validation
     * Decrypts the returning master user before returning it.
     */
 Task<ServerResponse> ReadMasterUserAsync(int masterUserId);
 
 /**
     * Sends a request to authenticate the master user.
     * Encrypts the credentials in MasterUserDTO, sends the request, and decrypts the server's response.
     * @param masterUserDto Data Transfer Object containing master user credentials.
     * @return ServerResponse with authentication status and details.
     */
 Task<ServerResponse> AuthenticateUserAsync(MasterUserDTO masterUserDto);
}