using System.Threading.Tasks;
using ServiceLayer.Services.LoginEntryService;
using ServiceLayer.Services.MasterUserService;
using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services;

public class ServiceLayerImpl : IServiceLayer
{
    private readonly ILoginEntryService _loginEntryService;
    private readonly IMasterUserService _masterUserService;

    public ServiceLayerImpl(ILoginEntryService loginEntryService,
        IMasterUserService masterUserService)
    {
        _loginEntryService = loginEntryService;
        _masterUserService = masterUserService;
    }

    public async Task<ServerResponse> CreateMasterUserAsync(
        MasterUserDTO masterUserDto)
    {
        return await _masterUserService.CreateMasterUserAsync(masterUserDto);
    }

    public async Task<ServerResponse> ReadMasterUserAsync(int masterUserId)
    {
        return await _masterUserService.ReadMasterUserAsync(masterUserId);
    }

    public async Task<ServerResponse> ReadLoginEntriesAsync()
    {
        return await _loginEntryService.ReadLoginEntriesAsync();
    }

    public async Task<ServerResponse> CreateLoginEntryAsync(
        LoginEntryDTO newEntry)
    {
        return await _loginEntryService.CreateLoginEntryAsync(newEntry);
    }

    public async Task<ServerResponse> UpdateLoginEntryAsync(
        LoginEntryDTO updatedEntry)
    {
        return await _loginEntryService.UpdateLoginEntryAsync(updatedEntry);
    }

    public async Task<ServerResponse> DeleteLoginEntryAsync(int entryId)
    {
        return await _loginEntryService.DeleteLoginEntryAsync(entryId);
    }
}