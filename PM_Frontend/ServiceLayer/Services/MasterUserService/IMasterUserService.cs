using Shared.Dtos;

namespace Client.Services.MasterUserService;
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
    Task<MasterUserDTO> CreateMasterUserAsync(MasterUserDTO masterUserDto);
    
    /**
     * Sends the read-request for the master user further down
     * Handles eventual validation
     * Decrypts the returning master user before returning it.
     */
    Task<MasterUserDTO> ReadMasterUserAsync(string masterUserId);
}