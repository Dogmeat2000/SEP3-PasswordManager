using System;
using System.Threading.Tasks;
using PM_Frontend.C#.Core;

namespace PM_Frontend.C#.Logic
{
    public class CommandLineInterface : ICommandLineInterface
    {

        public CommandLineInterface(PasswordManager passwordManager)
        {
            passwordManager = passwordManager;
        }

        public void Start()
        {
            while (true)
            {
                Console.WriteLine("Enter command (save/get): ");
                string command = Console.ReadLine();
                HandleCommand(command);
            }
        }

        public void HandleCommand(string command)
        {
            var args = command.Split(" ");
            switch (args[0])
            {
                case "save-password":
                    if (args.Length == 3)
                    {
                        passwordManager.SavePassword(args[1], args[2]);
                    }
                    else
                    {
                        Console.WriteLine("Use: save-password <username> <password>");
                    }
                    break;

                case "get-password":
                    if (args.Length == 2)
                    {
                        passwordManager.GetPassword(args[1]);
                    }
                    else
                    {
                        Console.WriteLine("Use: get-password <username>");
                    }
                    break;

                default:
                    Console.WriteLine("Not known command.");
                    break;
            }
        }
    }
}