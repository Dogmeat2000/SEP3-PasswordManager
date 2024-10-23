class Program
{
    static async Task Main(string[] args)
    {
        Console.WriteLine("Starting the application...");
        
        var loginEntryService = new LoginEntryService();
        var loginEntryManager = new LoginEntryManager(loginEntryService);
        
        // Test save a login entry
        Console.WriteLine("Testing CreateLoginEntry...");
        loginEntryManager.CreateLoginEntry("TestUser", "TestPassword123", 1);
        
        // Test getting the login entry, taking response time into account
        await Task.Delay(2000);
        
        Console.WriteLine("Testing ReadLoginEntry...");
        var password = await loginEntryService.ReadLoginEntry("TestUser");
        
        if (password != null)
        {
            Console.WriteLine($"Retrieved password: {password}");
        }
        else
        {
            Console.WriteLine("Failed to retrieve login entry.");
        }
        
        // Start CLI for manual testing
        var cli = new CommandLineInterfaceImpl();
        cli.Start();
    }
}