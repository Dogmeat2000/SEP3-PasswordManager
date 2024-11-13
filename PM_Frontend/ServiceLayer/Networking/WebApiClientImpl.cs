using System.Net.Http;
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
    private string WebApiUrl { get; set; }

    public WebApiClientImpl(HttpClient httpClient, string loadBalancerUrl)
    {
        _httpClient = httpClient;
        _loadBalancerUrl = loadBalancerUrl;
    }
    
    
    // MasterUser Methods:
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

    
    // LoginEntry methods:
    public async Task<ServerResponse> CreateLoginEntryAsync(LoginEntryDTO loginEntryDto)
    {
        var serverResponse =
            await SendRequestAsync <LoginEntryDTO>("CreateLoginEntry", loginEntryDto);
        return serverResponse;
    }
    
    public async Task<ServerResponse> ReadLoginEntriesAsync() {
        // Note: new LoginEntryDTO() is not used for anything in below. SendRequestAsync simply does not accept a DTO being NULL. 
        ServerResponse serverResponse = await SendRequestAsync("ReadLoginEntries", new LoginEntryDTO()); 
        return serverResponse;
    }

    /** Updates LoginEntry in DB **/
    public async Task<ServerResponse> UpdateLoginEntryAsync(LoginEntryDTO loginEntryDto)
    {
        var serverResponse = await SendRequestAsync("UpdateLoginEntry", loginEntryDto);
        return serverResponse;
    }

    /** Deletes LoginEntry in DB **/
    public async Task<ServerResponse> DeleteLoginEntryAsync(LoginEntryDTO logonEntryDto)
    {
        var serverResponse = await SendRequestAsync("DeleteLoginEntry", logonEntryDto);
        return serverResponse;
    }


    
    public async Task<ServerResponse> AuthenticateUserAsync(MasterUserDTO masterUserDto)
    {
        return await SendRequestAsync<MasterUserDTO>("AuthenticateUser", masterUserDto);
    }

    
    
    private async Task<ServerResponse> SendRequestAsync<TRequestDto>(string requestType, TRequestDto requestDto)
            where TRequestDto : DTO
        {
            //Create the CLientRequest to be sent to Web api
            var request = new ClientRequest(requestType, requestDto);
            
            //Check to see if WebApiUrl is set
            if (string.IsNullOrEmpty(WebApiUrl))
            {
                Console.WriteLine("Setting WebApiUrl");
                await SetWebApiServerUrlAsync(false);
            }

            var serverOverloaded = true;
            var numberOfAttempts = 0;

            //if current web api server is overloaded, get another url
            while (serverOverloaded && numberOfAttempts < 5)
            {
                Console.WriteLine("Attempting to use WEB-API URL: " + WebApiUrl + " for request of type: " + requestType);

                // Serialize the request with type information
                var jsonRequest = JsonConvert.SerializeObject(request, new JsonSerializerSettings
                {
                    TypeNameHandling = TypeNameHandling.Auto // Ensure @class is included for polymorphism
                });
                
                //Send the request to the web api server
                var response = await _httpClient.PostAsync(WebApiUrl, new StringContent(jsonRequest, Encoding.UTF8, "application/json"));
                Console.WriteLine("Response status code: " + response.StatusCode);

                if (!response.IsSuccessStatusCode)
                {
                    // Check for overload and other potential errors
                    if (response.StatusCode == System.Net.HttpStatusCode.ServiceUnavailable)
                    {
                        //Server is overloaded, or otherwise unavailable, get new web api url
                        Console.WriteLine("Server is overloaded, trying new server:");
                        await WebApiServerIsOverloadedAsync();
                        numberOfAttempts++;
                    }
                    else
                    {
                        throw new Exception("Server responded with an error: " + response.ReasonPhrase);
                    }
                }
                else
                {
                    //Succes case
                    var jsonResponse = await response.Content.ReadAsStringAsync();

                    // Deserialize ServerResponse to determine if we received a valid DTO
                    var serverResponse = JsonConvert.DeserializeObject<ServerResponse>(jsonResponse);
                    Console.WriteLine("ServerResponse: " + serverResponse);
                    
                    if (serverResponse != null)
                    {
                        return serverResponse;
                    }
                    
                    throw new Exception("Server was not responded with an error: " + jsonResponse);
                    
                    
                }
            }

            throw new Exception("Server overloaded. Max attempts reached.");
        }
        

    private async Task<string> SetWebApiServerUrlAsync(bool overLoaded)
    {
        //if webapi url not already set
        if (string.IsNullOrEmpty(WebApiUrl) || overLoaded)
        {
            //Ask loadbalancer for new web api server
            ClientRequest request = new ClientRequest("GetAvailableServer", null);
            var response = await _httpClient.PostAsJsonAsync($"{_loadBalancerUrl}/loadbalancer/server", request);

            response.EnsureSuccessStatusCode();

            var serverResponse = await response.Content.ReadFromJsonAsync<ServerResponse>();
            //Set webapiurl to be the new url
            WebApiUrl = serverResponse?.message + "/api/handleRequest";
            Console.WriteLine("WebApiUrl set to: " + WebApiUrl);
        }

        return WebApiUrl;
    }

    private async Task WebApiServerIsOverloadedAsync()
    {
        var newServerUrl = await SetWebApiServerUrlAsync(true);
        Console.WriteLine("New server reached with URL: " + newServerUrl);
    }
}
