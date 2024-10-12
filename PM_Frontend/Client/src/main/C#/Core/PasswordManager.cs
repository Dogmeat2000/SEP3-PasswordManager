using PM_Frontend.C#.Services;

namespace PM_Frontend.C#.Core
{
    public class PasswordManager
    {
        public PasswordManager(PasswordService passwordService)
        {
            passwordService = passwordService;
        }

        public void SavePassword(string username, string password)
        {
            passwordService.SavePassword(username, password);
        }

        public void GetPassword(string username)
        {
            var password = passwordService.GetPassword(username);
            if (password != null)
            {
                System.Console.WriteLine($"Password for {username}: {password}");
            }
            else
            //Simple check, can be changed when we do more with secuirty
            {
                System.Console.WriteLine($"No password found for {username}");
            }
        }
    }
}