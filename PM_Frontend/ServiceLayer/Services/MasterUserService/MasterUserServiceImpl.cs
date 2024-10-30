using ServiceLayer.Networking;
using ServiceLayer.Services.Cryptography;
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

    public async Task<MasterUserDTO> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        //TODO: ?? Add validation on the master user before it is encrypted

        //Encrypt the master user
        var encryptedMasterUserDto = await _cryptographyService.EncryptMasterUserAsync(masterUserDto);

        //Send the request to the web api
        var returnedMasterUserDto = await _webApiClient.CreateMasterUserAsync(encryptedMasterUserDto);

        //TODO: ?? Add validation that the returned masteruser is the same as the encrypted one sent

        //Decrypt master user
        var decryptedMasterUserDto = await _cryptographyService.DecryptMasterUserAsync(returnedMasterUserDto);

        //TODO: ?? Add validation that the returned decrypted masteruser is the same as the one sent

        //Return the decrypted master user
        return decryptedMasterUserDto;
    }

    public async Task<MasterUserDTO> ReadMasterUserAsync(int masterUserId)
    {
        //Send the request to the web-api
        var returnedMasterUserDto = await _webApiClient.ReadMasterUserAsync(masterUserId);

        //TODO: ?? Validation

        return returnedMasterUserDto;
    }
}