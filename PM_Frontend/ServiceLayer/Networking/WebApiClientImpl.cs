using System.Net;
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

    public WebApiClientImpl(HttpClient httpClient, string loadBalancerUrl) {
        _httpClient = httpClient;
        _loadBalancerUrl = loadBalancerUrl;
        ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;
    }
    
    
    // MasterUser Methods:
    public async Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        var responseMasterUserDto =
            await SendRequestAsync<MasterUserDTO>("CreateMasterUser", masterUserDto);
        return responseMasterUserDto;
    }

    public async Task<ServerResponse> ReadMasterUserAsync(MasterUserDTO masterUserDto)
    {
        var serverResponse = await SendRequestAsync<MasterUserDTO>("ReadMasterUser", masterUserDto);
        return serverResponse;
    }

    
    // LoginEntry methods:
    public async Task<ServerResponse> CreateLoginEntryAsync(LoginEntryDTO newEntry)
    {
        var serverResponse =
            await SendRequestAsync <LoginEntryDTO>("CreateLoginEntry", newEntry);
        return serverResponse;
    }
    
    public async Task<ServerResponse> ReadLoginEntriesAsync(MasterUserDTO dto) {
        ServerResponse serverResponse = await SendRequestAsync("ReadLoginEntries", dto);
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


    
    // TODO: Marcus, commented out due to errors.
    /*public async Task<ServerResponse> AuthenticateUserAsync(MasterUserDTO masterUserDto)
    {
        return await SendRequestAsync<MasterUserDTO>("AuthenticateUser", masterUserDto);
    }*/

    
    
    private async Task<ServerResponse> SendRequestAsync<TRequestDto>(string requestType, TRequestDto requestDto)
            where TRequestDto : DTO
        {
            //Create the CLientRequest to be sent to Web api
            var request = new ClientRequest(requestType, requestDto);
            
            //Check to see if WebApiUrl is set
            if (string.IsNullOrEmpty(WebApiUrl)) {
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
                var jsonRequest = JsonConvert.SerializeObject(request, new JsonSerializerSettings {
                    TypeNameHandling = TypeNameHandling.Auto // Ensure @class is included for polymorphism
                });
                Console.WriteLine(jsonRequest);
                
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
                    } else {
                        NetworkExceptionHandler.HandleException(response.StatusCode, response.Content.ReadAsStringAsync().Result);
                        throw new Exception("Server responded with an error: " + response.ReasonPhrase);
                    }
                }
                else
                {
                    //Succes case
                    var jsonResponse = await response.Content.ReadAsStringAsync();

                    // Deserialize ServerResponse to determine if we received a valid DTO
                    var serverResponse = JsonConvert.DeserializeObject<ServerResponse>(jsonResponse, new JsonSerializerSettings
                    {
                        TypeNameHandling = TypeNameHandling.Auto
                    });
                    
                    Console.WriteLine("ServerResponse: " + serverResponse);
                    
                    if (serverResponse != null) {
                        return serverResponse;
                    }
                    
                    throw new Exception("Server was not responded with an error: " + jsonResponse);
                }
            }

            throw new Exception("Server overloaded. Max attempts reached.");
        }
        

    private async Task<string> SetWebApiServerUrlAsync(bool overLoaded)
    {
        try {
            //if webapi url not already set
            if (string.IsNullOrEmpty(WebApiUrl) || overLoaded) {
                
                //Ask loadbalancer for new web api server
                ClientRequest request = new ClientRequest("GetAvailableServer", null);
                var response = await _httpClient.PostAsJsonAsync($"{_loadBalancerUrl}/loadbalancer/server", request);
                response.EnsureSuccessStatusCode();
                var serverResponse = await response.Content.ReadFromJsonAsync<ServerResponse>();
                //Set webapiurl to be the new url
                
                Console.WriteLine("Response from LoadBalancer: " + response);
                if (serverResponse == null) 
                {
                    Console.WriteLine("LoadBalancer did not return a valid response.");
                    throw new Exception("LoadBalancer did not return a valid server URL.");
                }
                Console.WriteLine("ServerResponse message: " + serverResponse.message);

                WebApiUrl = serverResponse?.message + "/api/handleRequest";
                Console.WriteLine("WebApiUrl set to: " + WebApiUrl);
            }
        }
        catch (Exception e) {
            Console.WriteLine(e.StackTrace);
            throw new Exception($"SetWebApiServerUrlAsync failed: {e.Message}");
        }


        return WebApiUrl;
    }

    private async Task WebApiServerIsOverloadedAsync()
    {
        var newServerUrl = await SetWebApiServerUrlAsync(true);
        Console.WriteLine("New server reached with URL: " + newServerUrl);
    }
}
