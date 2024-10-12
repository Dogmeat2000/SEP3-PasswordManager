using System.Threading.Tasks;
using System;
using PM_Frontend.C#.Core;
using PM_Frontend.C#.Logic;
using PM_Frontend.C#.Services;

namespace PM_Frontend
    {
        class Program
        {
            static void Main(string[] args)
            {
                Console.WriteLine("Starting the application...");
                var passwordService = new PasswordService();
                var passwordManager = new PasswordManager(passwordService);
                var cli = new ClientInterface(passwordManager);

                cli.Start();
            }
        }
    }
