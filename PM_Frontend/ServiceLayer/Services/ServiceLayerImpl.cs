using Client.Services.LoginEntryService;
using Client.Services.MasterUserService;
using Shared.Dtos;

namespace Client.Services;

public class ServiceLayerImpl : IServiceLayer
{
    private readonly ILoginEntryService _loginEntryService;
    private readonly IMasterUserService _masterUserService;

    public ServiceLayerImpl(ILoginEntryService loginEntryService, IMasterUserService masterUserService)
    {
        _loginEntryService = loginEntryService;
        _masterUserService = masterUserService;
    }

    public Task<MasterUserDTO> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        return _masterUserService.CreateMasterUserAsync(masterUserDto);
    }

    public Task<MasterUserDTO> ReadMasterUserAsync(string masterUserId)
    {
        return _masterUserService.ReadMasterUserAsync(masterUserId);
    }
}