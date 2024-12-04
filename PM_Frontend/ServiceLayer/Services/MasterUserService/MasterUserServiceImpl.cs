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
        var returnedServerResponse = await _webApiClient.CreateMasterUserAsync(masterUserDto);
        
        return returnedServerResponse;
    }

    
    public async Task<ServerResponse> ReadMasterUserAsync(MasterUserDTO masterUserDto)
    {
        var returnedMasterUserDto = await _webApiClient.ReadMasterUserAsync(masterUserDto);
        

        return returnedMasterUserDto;
    }
    
}