using System.Net.Http.Json;
using System.Text;
using Newtonsoft.Json; // Make sure to add this
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
        // Create a ClientRequest with requestType and requestDto
        var request = new ClientRequest(requestType, requestDto);
        await SetWebApiServerUrlAsync(false);

        var serverOverloaded = true;
        var numberOfAttempts = 0;

        while (serverOverloaded && numberOfAttempts < 5)
        {
            Console.WriteLine("From WebApiClientImpl.cs: Attempting to use WEB-API URL: " + WebApiUrl +
                              " for request of type: " + requestType);

            // Serialize the request using Newtonsoft.Json
            var jsonRequest = JsonConvert.SerializeObject(request, new JsonSerializerSettings
            {
                TypeNameHandling = TypeNameHandling.Objects // This ensures @class is included
            });

            // Sending the request as JSON
            var response = await _httpClient.PostAsync(WebApiUrl, new StringContent(jsonRequest, Encoding.UTF8, "application/json"));

            // Read the server's response
            var serverResponse = await response.Content.ReadFromJsonAsync<ServerResponse>();

            // Handle overloaded server responses
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

    private async Task<string> SetWebApiServerUrlAsync(bool overLoaded)
    {
        if (!string.IsNullOrEmpty(WebApiUrl) && !overLoaded) return WebApiUrl;
        ClientRequest request = new ClientRequest("GetAvailableServer", null);
        var response = await _httpClient.PostAsJsonAsync($"{_loadBalancerUrl}/loadbalancer/server", request);

        response.EnsureSuccessStatusCode();

        var serverResponse = await response.Content.ReadFromJsonAsync<ServerResponse>();
        WebApiUrl = serverResponse?.Message + "/api/handleRequest";
        return serverResponse?.Message;
    }

    private async Task WebApiServerIsOverloadedAsync(ClientRequest request)
    {
        var newServerUrl = await SetWebApiServerUrlAsync(true);
        Console.WriteLine("New server reached with URL: " + newServerUrl);
    }
}
