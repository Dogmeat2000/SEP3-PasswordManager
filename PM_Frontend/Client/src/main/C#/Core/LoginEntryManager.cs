using PM_Frontend.C#.Services;

namespace PM_Frontend.C#.Core
{
public class LoginEntryManager
{
    private readonly LoginEntryService _loginEntryService;

    public LoginEntryManager(LoginEntryService loginEntryService)
    {
        _loginEntryService = loginEntryService;
    }

    public void CreateLoginEntry(string entryUsername, string entryPassword, int masterUserId)
    {
        _loginEntryService.CreateLoginEntry(entryUsername, entryPassword, masterUserId);
    }

    public async Task<string> ReadLoginEntry(string entryUsername)
    {
        return await _loginEntryService.ReadLoginEntry(entryUsername);
    }
}
}