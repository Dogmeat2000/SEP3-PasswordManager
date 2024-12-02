using ServiceLayer.Services.LoginEntryService;
using ServiceLayer.Services.MasterUserService;
using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services;

public class ServiceLayerImpl : IServiceLayer
{
    private readonly ILoginEntryService _loginEntryService;
    private readonly IMasterUserService _masterUserService;

    public ServiceLayerImpl(ILoginEntryService loginEntryService, IMasterUserService masterUserService) {
        _loginEntryService = loginEntryService;
        _masterUserService = masterUserService;
    }

    // MasterUser Service Methods:
    public async Task<ServerResponse> CreateMasterUserAsync(
        MasterUserDTO masterUserDto) {
        return await _masterUserService.CreateMasterUserAsync(masterUserDto);
    }

    public async Task<ServerResponse> ReadMasterUserAsync(MasterUserDTO masterUserDto)
    {
        return await _masterUserService.ReadMasterUserAsync(masterUserDto);
    }
    

    // LoginEntry Service Methods:
    public async Task<ServerResponse> ReadLoginEntriesAsync(MasterUserDTO dto) {
        return await _loginEntryService.ReadLoginEntriesAsync(dto);
    }

    public async Task<ServerResponse> CreateLoginEntryAsync(
        LoginEntryDTO newEntry)
    {
        ServerResponse serverResponse = await _loginEntryService.CreateLoginEntryAsync(newEntry);
        Console.WriteLine("ServiceLayerImpl: Status code:" + serverResponse.statusCode);
        return serverResponse;
    }

    public async Task<LoginEntryDTO> UpdateLoginEntryAsync(LoginEntryDTO updatedEntry)
    {
        return await _loginEntryService.UpdateLoginEntryAsync(updatedEntry);
    }

    public async Task<bool> DeleteLoginEntryAsync(LoginEntryDTO entryToDelete)
    {
        return await _loginEntryService.DeleteLoginEntryAsync(entryToDelete);
    }
}