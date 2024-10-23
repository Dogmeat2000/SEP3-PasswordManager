using System;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace PM_Frontend.C#.Services
{
    public class LoginEntryService
    {
        private readonly HttpClient _httpClient;

        public LoginEntryService()
        {
            _httpClient = new HttpClient();
        }

        public async void CreateLoginEntry(string entryUsername, string entryPassword, int masterUserId)
        {
            // Create DTO object for Login Entry
            var loginEntryDto = new LoginEntryDTO(entryUsername, entryPassword, masterUserId);

            // Create ClientRequest with the appropriate request type
            var request = new ClientRequest("CreateLoginEntry", loginEntryDto);

            // Serialize the request to JSON
            var jsonContent = JsonSerializer.Serialize(request);
            var httpContent = new StringContent(jsonContent, Encoding.UTF8, "application/json");

            // Send request to load balancer or WebAPI
            var response = await _httpClient.PostAsync("http://loadbalancer-url/api/assign-server", httpContent);

            // Handle the responses
            if (response.IsSuccessStatusCode)
            {
                Console.WriteLine("Login entry saved successfully.");
            }
            else
            {
                Console.WriteLine("Failed to save login entry.");
            }
        }

        public async Task<string> ReadLoginEntry(string entryUsername)
        {
            // Send GET request to retrieve password for user
            var response = await _httpClient.GetAsync($"http://loadbalancer-url/api/read-login-entry?entryUsername={entryUsername}");

            if (response.IsSuccessStatusCode)
            {
                var passwordJson = await response.Content.ReadAsStringAsync();
                return JsonSerializer.Deserialize<string>(passwordJson);
            }
            else
            {
                Console.WriteLine("Failed to retrieve login entry.");
                return null;
            }
        }
    }
}
