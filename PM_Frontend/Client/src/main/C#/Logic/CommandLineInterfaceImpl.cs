class CommandLineInterfaceImpl
{
    private readonly LoginEntryService _loginEntryService;

    public CommandLineInterfaceImpl()
    {
        _loginEntryService = new LoginEntryService();
    }

    public async Task HandleCommand(string command)
    {
        var args = command.Split(" ");
        
        switch (args[0])
        {
            case "create-login-entry":
                if (args.Length == 4)
                {
                    _loginEntryService.CreateLoginEntry(args[1], args[2], int.Parse(args[3]));
                }
                else
                {
                    Console.WriteLine("Use: create-login-entry <entryUsername> <entryPassword> <masterUserId>");
                }
                break;

            case "read-login-entry":
                if (args.Length == 2)
                {
                    var password = await _loginEntryService.ReadLoginEntry(args[1]);
                    if (password != null)
                    {
                        Console.WriteLine($"Password for {args[1]}: {password}");
                    }
                    else
                    {
                        Console.WriteLine("No login entry found.");
                    }
                }
                else
                {
                    Console.WriteLine("Use: read-login-entry <entryUsername>");
                }
                break;

            default:
                Console.WriteLine("Unknown command.");
                break;
        }
    }
}