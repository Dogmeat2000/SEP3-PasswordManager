using System.Net.Http.Json;
using System.Text;
using Newtonsoft.Json; // Make sure to add this
using Shared.CommunicationObjects;
using Shared.Dtos;
using Shared.JSONService;

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

    public async Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        var responseMasterUserDto =
            await SendRequestAsync<MasterUserDTO>("CreateMasterUser", masterUserDto);
        return responseMasterUserDto;
    }

    public async Task<ServerResponse> ReadMasterUserAsync(int masterUserId)
    {
        var responseMasterUserDto =
            await SendRequestAsync<MasterUserDTO>("ReadMasterUser",
                new MasterUserDTO(masterUserId, null, null));
        return responseMasterUserDto;
    }

    private async Task<ServerResponse> SendRequestAsync<TRequestDto>(string requestType, TRequestDto requestDto)
            where TRequestDto : DTO
        {
            var request = new ClientRequest(requestType, requestDto);
            await SetWebApiServerUrlAsync(false);

            var serverOverloaded = true;
            var numberOfAttempts = 0;

            while (serverOverloaded && numberOfAttempts < 5)
            {
                Console.WriteLine("Attempting to use WEB-API URL: " + WebApiUrl + " for request of type: " + requestType);

                // Serialize the request with type information
                var jsonRequest = JsonConvert.SerializeObject(request, new JsonSerializerSettings
                {
                    TypeNameHandling = TypeNameHandling.Auto // Ensure @class is included for polymorphism
                });
                
                Console.WriteLine("JSon request: " + jsonRequest);

                var response = await _httpClient.PostAsync(WebApiUrl, new StringContent(jsonRequest, Encoding.UTF8, "application/json"));
                
                Console.WriteLine("Response status code: " + response.StatusCode);
                Console.WriteLine(response.Content.ReadAsStringAsync().Result);

                if (!response.IsSuccessStatusCode)
                {
                    // Check for overload and other potential errors
                    if (response.StatusCode == System.Net.HttpStatusCode.ServiceUnavailable)
                    {
                        Console.WriteLine("Server is overloaded, trying new server:");
                        await WebApiServerIsOverloadedAsync(request);
                        numberOfAttempts++;
                    }
                    else
                    {
                        throw new Exception("Server responded with an error: " + response.ReasonPhrase);
                    }
                }
                else
                {
                    var jsonResponse = await response.Content.ReadAsStringAsync();
                    Console.WriteLine(jsonResponse);

                    // Deserialize ServerResponse to determine if we received a valid DTO
                    var serverResponse = JsonConvert.DeserializeObject<ServerResponse>(jsonResponse);
                    Console.WriteLine("ServerResponse: " + serverResponse);
                    
                        /*, new JsonSerializerSettings
                    {
                        TypeNameHandling = TypeNameHandling.Objects,
                        Converters = new List<JsonConverter> { new DTOJsonConverter() } // Only use the custom converter
                    });
*/
                    

                    if (serverResponse != null)
                    {
                        return serverResponse;
                    }

                    else
                    {
                        throw new Exception("Server was not responded with an error: " + jsonResponse);
                    }
                    
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
        WebApiUrl = serverResponse?.message + "/api/handleRequest";
        return serverResponse?.message;
    }

    private async Task WebApiServerIsOverloadedAsync(ClientRequest request)
    {
        var newServerUrl = await SetWebApiServerUrlAsync(true);
        Console.WriteLine("New server reached with URL: " + newServerUrl);
    }
}
