using System.Net.Http.Json;
using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Networking;

public class WebApiClientImpl : IWebApiClient
{
    private readonly HttpClient _httpClient;
    private readonly string _loadBalancerUrl;

    public WebApiClientImpl(HttpClient httpClient, string loadBalancerUrl)
    {
        _httpClient = httpClient;
        _loadBalancerUrl = loadBalancerUrl;
    }

    private string? WebApiUrl { get; set; }

    public async Task<MasterUserDTO> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        var responseMasterUserDto =
            await SendRequestAsync<MasterUserDTO, MasterUserDTO>("CreateMasterUser", masterUserDto);
        return responseMasterUserDto;
    }

    public async Task<MasterUserDTO> ReadMasterUserAsync(int masterUserId)
    {
        var responseMasterUserDto =
            await SendRequestAsync<MasterUserDTO, MasterUserDTO>("ReadMasterUser",
                new MasterUserDTO(masterUserId, null, null));
        return responseMasterUserDto;
    }

    private async Task<TResponseDto> SendRequestAsync<TRequestDto, TResponseDto>(string requestType,
        TRequestDto requestDto)
        where TRequestDto : DTO
        where TResponseDto : DTO
    {
        var request = new ClientRequest(requestType, requestDto);
        await SetWebApiServerUrlAsync(request, false);

        var serverOverloaded = true;
        var numberOfAttempts = 0;

        while (serverOverloaded && numberOfAttempts < 5)
        {
            Console.WriteLine("From WebApiClientImpl.cs: Attempting to use WEB-API URL: " + WebApiUrl +
                              " for request of type: " + requestType);
            var response = await _httpClient.PostAsJsonAsync(WebApiUrl, request);

            var serverResponse = await response.Content.ReadFromJsonAsync<ServerResponse>();

            if (serverResponse != null && serverResponse.StatusCode == 503)
            {
                Console.WriteLine("From WebApiClientImpl.cs: Server is overloaded, getting new server:");
                await WebApiServerIsOverloadedAsync(request);
                numberOfAttempts++;
            }
            else if (serverResponse != null && serverResponse.Dto is TResponseDto responseDto)
            {
                // Successfully received a valid response
                return responseDto;
            }
            else
            {
                // Handle other possible response scenarios or throw an exception if needed
                throw new Exception("Unexpected response from the server.");
            }
        }

        throw new Exception("Server overloaded. Max attempts reached.");
    }

    private async Task<string> SetWebApiServerUrlAsync(ClientRequest request, bool overLoaded)
    {
        if (!string.IsNullOrEmpty(WebApiUrl) && !overLoaded) return WebApiUrl;
        var response = await _httpClient.PostAsJsonAsync($"{_loadBalancerUrl}/loadbalancer/server", request);

        response.EnsureSuccessStatusCode();

        var serverResponse = await response.Content.ReadFromJsonAsync<ServerResponse>();
        WebApiUrl = serverResponse?.Message;
        return serverResponse?.Message;
    }

    private async Task WebApiServerIsOverloadedAsync(ClientRequest request)
    {
        var newServerUrl = await SetWebApiServerUrlAsync(request, true);
        Console.WriteLine("New server reached with URL: " + newServerUrl);
    }
}