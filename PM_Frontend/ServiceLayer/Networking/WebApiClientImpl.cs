using Shared.Dtos;

namespace ServiceLayer.Networking;

public class WebApiClientImpl : IWebApiClient
{
    private readonly HttpClient _httpClient;

    public WebApiClientImpl(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }
    
    public Task<MasterUserDTO> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        throw new NotImplementedException();
    }

    public Task<MasterUserDTO> ReadMasterUserAsync(string masterUserId)
    {
        throw new NotImplementedException();
    }
}