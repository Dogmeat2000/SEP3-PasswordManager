using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.Cryptography;

public interface ICryptographyService
{
    Task<MasterUserDTO> EncryptMasterUserAsync(MasterUserDTO masterUserDTO);
    Task<ServerResponse> DecryptServerResponceAsync(ServerResponse serverResponse);
    Task<LoginEntryDTO> EncryptLoginEntryAsync(LoginEntryDTO loginEntryDTO);
    Task<ServerResponse> DecryptLoginEntryAsync(ServerResponse serverResponse);
    
    /**<summary>
     * Handles decryption of the <see cref="LoginEntryListDTO"/>, where embedded Login Entries are decrypted and repacked into this DTO.
     * </summary>
     * 
     * <remarks>Currently this method tries to decrypt data. If it fails, it just displays it in its encrypted form.</remarks>
     * 
     * <param name="serverResponse">An encrypted <see cref="ServerResponse"/> object that contains the <see cref="LoginEntryListDTO"/> to decrypt</param>
     *
     * <returns> <p>A <see cref="ServerResponse"/> object containing the <b>decrypted</b> DTO of <see cref="LoginEntryListDTO"/> type, associated with the logged-in user.</p>
     * </returns>*/
    Task<ServerResponse> DecryptLoginEntryListAsync(ServerResponse serverResponse);
}