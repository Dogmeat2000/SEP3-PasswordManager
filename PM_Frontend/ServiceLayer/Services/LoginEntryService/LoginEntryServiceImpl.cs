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

    public async Task<ServerResponse> ReadLoginEntriesAsync(MasterUserDTO dto) {
        // Validate received request, before encryption:
        // TODO: Finish implementing this validation, once authorization and login functionality have been completed.
        /*if (dto.id != loggedInUser.id || dto.masterUsername != loggedInUser.masterUsername || dto.masterPassword != loggedInUser.masterPassword) {
            throw new ArgumentException("Unable to process request. Valid logged-in user credentials were not provided.");
        }*/
        
        // Encrypt the embedded dto:
        // TODO: Encryption / Decryption does not reliably produce the same encrypted strings each time... So for now it does not work.
        //var encryptedDto = await _cryptographyService.EncryptMasterUserAsync(dto);
        var encryptedDto = dto; // TODO: Temporary solution.
        
        // Request all loginEntries
        ServerResponse response = await _webApiClient.ReadLoginEntriesAsync(encryptedDto);
        
        // Validate the response:
        if (!(response.dto != null && response.dto.GetType() == typeof(LoginEntryListDTO))) {
            throw new ApplicationException("Failed to fetch all login entries.");
        }
        
        // Check query was success, else return the exception response:
        if (response.statusCode != 200) {
            throw new ApplicationException("Failed to fetch login entries.");
        }
        
        // Decrypt the embedded loginEntries.
        //ServerResponse decryptedResponse = await _cryptographyService.DecryptLoginEntryListAsync(response);
        ServerResponse decryptedResponse = response; // TODO: Temporary solution.
        
        // Return the ServerResponse
        return decryptedResponse;
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