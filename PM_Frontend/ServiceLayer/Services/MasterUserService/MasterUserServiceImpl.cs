using ServiceLayer.Networking;
using ServiceLayer.Services.Cryptography;
using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.MasterUserService;

public class MasterUserServiceImpl : IMasterUserService
{
    private readonly ICryptographyService _cryptographyService;
    private readonly IWebApiClient _webApiClient;

    public MasterUserServiceImpl(ICryptographyService cryptographyService, IWebApiClient webApiClient)
    {
        _cryptographyService = cryptographyService;
        _webApiClient = webApiClient;
    }

    public async Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        //TODO: ?? Add validation on the master user before it is encrypted

        //Encrypt the master user
        var encryptedMasterUserDto = await _cryptographyService.EncryptMasterUserAsync(masterUserDto);

        //Send the request to the web api
        var returnedServerResponse = await _webApiClient.CreateMasterUserAsync(encryptedMasterUserDto);

        //TODO: ?? Add validation that the returned masteruser is the same as the encrypted one sent

        //Decrypt Server response
        var decryptedServerResponse = await _cryptographyService.DecryptServerResponceAsync(returnedServerResponse);

        //TODO: ?? Add validation that the returned decrypted masteruser is the same as the one sent

        //Return the decrypted master user
        return decryptedServerResponse;
    }

    public async Task<ServerResponse> ReadMasterUserAsync(int masterUserId)
    {
        //Send the request to the web-api
        var returnedMasterUserDto = await _webApiClient.ReadMasterUserAsync(masterUserId);

        //TODO: ?? Validation

        return returnedMasterUserDto;
    }

    // TODO: Marcus, commented out due to errors.
    /*public async Task<ServerResponse> AuthenticateUserAsync(MasterUserDTO masterUserDto)
    {
        // Encrypt the master user credentials for secure transmission
        var encryptedMasterUserDto = await _cryptographyService.EncryptMasterUserAsync(masterUserDto);

        // Send encrypted credentials to the web API for authentication
        var response = await _webApiClient.AuthenticateUserAsync(encryptedMasterUserDto);

        // Decrypt the response to retrieve authentication results
        return await _cryptographyService.DecryptServerResponceAsync(response);
        
    }*/
}