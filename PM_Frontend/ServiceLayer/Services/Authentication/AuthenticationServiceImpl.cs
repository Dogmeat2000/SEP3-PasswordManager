using System.Net.Http;
using System.Text;
using System.Text.Json;
using Newtonsoft.Json;
using ServiceLayer.Services.Authentication;
using Shared.Dtos;

namespace ServiceLayer.Authentication
{
    public class AuthenticationServiceImpl : IAuthenticationService
    {
        private readonly HttpClient _httpClient;

        public AuthenticationServiceImpl(HttpClient httpClient)
        {
            _httpClient = httpClient;
        }

        public async Task<MasterUserDTO?> AuthenticateUserAsync(string username, string password)
        {
            // Create the MasterUserDTO request
            var loginRequest = new MasterUserDTO
            {
                masterUsername = username,
                masterPassword = password
            };

            // Serialize the DTO and send it to the API
            var requestContent = new StringContent(
                JsonConvert.SerializeObject(loginRequest),
                Encoding.UTF8,
                "application/json");

            var response = await _httpClient.PostAsync("auth/login", requestContent);
            
            if (response.IsSuccessStatusCode)
            {
                var jsonResponse = await response.Content.ReadAsStringAsync();
                return JsonConvert.DeserializeObject<MasterUserDTO>(jsonResponse);
            }

            return null; 
        }
    }
}