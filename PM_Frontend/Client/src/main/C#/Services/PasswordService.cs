using System;

namespace PM_Frontend.C#.Services
{
    public class PasswordService
    {
        public void SavePassword(string username, string password)
        {
            // Simulating password save requests
            Console.WriteLine($"Sends save request for username: {username}");
            // TODO: implement actual logic to call REST or WEBAPI services..
        }

        public string GetPassword(string username)
        {
            // Simulating password fetch from backend.
            Console.WriteLine($"Sends get request for username: {username}");
            // Return a simulated password, TODO: Actual call to backend here.
            return "encrypted-password";
            // TODO: Encryption, but thats for another time
        }
    }
}