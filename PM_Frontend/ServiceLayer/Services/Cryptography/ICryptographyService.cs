using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.Cryptography;

/// <summary>
/// Interface for cryptography services, providing methods to encrypt and decrypt various data objects.
/// </summary>
public interface ICryptographyService
{
    /// <summary>
    /// Encrypts the provided MasterUserDTO object.
    /// </summary>
    /// <param name="masterUserDTO">The MasterUserDTO object to encrypt.</param>
    /// <returns>A Task representing the asynchronous operation. 
    /// The result contains the encrypted MasterUserDTO object.</returns>
    Task<MasterUserDTO> EncryptMasterUserAsync(MasterUserDTO masterUserDTO);
    
    /// <summary>
    /// Decrypts the provided ServerResponse object.
    /// </summary>
    /// <param name="serverResponse">The ServerResponse object to decrypt.</param>
    /// <returns>A Task representing the asynchronous operation. 
    /// The result contains the decrypted ServerResponse object.</returns>
    Task<ServerResponse> DecryptServerResponceAsync(ServerResponse serverResponse);
    
    /// <summary>
    /// Encrypts the provided LoginEntryDTO object.
    /// </summary>
    /// <param name="loginEntryDTO">The LoginEntryDTO object to encrypt.</param>
    /// <returns>A Task representing the asynchronous operation. 
    /// The result contains the encrypted LoginEntryDTO object.</returns>
    Task<LoginEntryDTO> EncryptLoginEntryAsync(LoginEntryDTO loginEntryDTO);
    
    /// <summary>
    /// Decrypts the LoginEntry information in the provided ServerResponse object.
    /// </summary>
    /// <param name="serverResponse">The ServerResponse object containing the LoginEntry to decrypt.</param>
    /// <returns>A Task representing the asynchronous operation. 
    /// The result contains the ServerResponse object with decrypted LoginEntry data.</returns>
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