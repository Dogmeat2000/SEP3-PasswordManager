using ServiceLayer.Networking;
using ServiceLayer.Services.Cryptography;
using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.LoginEntryService;

public class LoginEntryServiceImpl : ILoginEntryService
{
    private readonly ICryptographyService _cryptographyService;
    private readonly IWebApiClient _webApiClient;

    public LoginEntryServiceImpl(ICryptographyService cryptographyService,
        IWebApiClient webApiClient)
    {
        _cryptographyService = cryptographyService;
        _webApiClient = webApiClient;
    }

    /**
    * Retrieves all login entries from the server, decrypting the passwords before returning.
    *
    * @return ServerResponse containing the list of decrypted LoginEntryDTOs.
    */
    public async Task<ServerResponse> ReadLoginEntriesAsync()
    {
        return null;
    }

    /**
     * Creates a new login entry, encrypting the password before sending it to the server.
     * @param newEntry LoginEntryDTO containing the data for the new entry.
     * @return ServerResponse containing the created entry.
     */
    public async Task<ServerResponse> CreateLoginEntryAsync(
        LoginEntryDTO newEntry)
    {
        ServerResponse response = await _webApiClient.CreateLoginEntryAsync(await _cryptographyService.EncryptLoginEntryAsync(newEntry));
        return await _cryptographyService.DecryptLoginEntryAsync(response);
    }

    /**
     * Updates an existing login entry, encrypting the password before sending it to the server.
     * @param updatedEntry LoginEntryDTO containing the updated data.
     * @return ServerResponse containing the updated entry.
     */
    public async Task<LoginEntryDTO> UpdateLoginEntryAsync(LoginEntryDTO updatedEntry)
    {
        var encryptedEntry = await _cryptographyService.EncryptLoginEntryAsync(updatedEntry);
        
        var response = await _webApiClient.UpdateLoginEntryAsync(encryptedEntry);
        
        var decryptedEntry = await _cryptographyService.DecryptLoginEntryAsync(response);
        var decryptedEntryDto = decryptedEntry.dto;

        if (!(decryptedEntryDto.GetType() == typeof(LoginEntryDTO)))
        {
            throw new ApplicationException("Failed to update login entry");
        }
    
        return (LoginEntryDTO)decryptedEntryDto;
    }

    /**
     * Deletes a login entry by its ID.
     * @param entryId The ID of the entry to delete.
     * @return ServerResponse indicating success or failure of the deletion.
     */
    public async Task<bool> DeleteLoginEntryAsync(LoginEntryDTO entryToDelete)
    {
        var response = await _webApiClient.DeleteLoginEntryAsync(entryToDelete);
        return true; // TO DO Implement proper validation
    }
}