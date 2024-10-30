using ServiceLayer.Services.LoginEntryService;
using ServiceLayer.Services.MasterUserService;
using Shared.Dtos;

namespace ServiceLayer.Services;

public class ServiceLayerImpl : IServiceLayer
{
    private readonly ILoginEntryService _loginEntryService;
    private readonly IMasterUserService _masterUserService;

    public ServiceLayerImpl(ILoginEntryService loginEntryService, IMasterUserService masterUserService)
    {
        _loginEntryService = loginEntryService;
        _masterUserService = masterUserService;
    }

    public async Task<MasterUserDTO> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        return await _masterUserService.CreateMasterUserAsync(masterUserDto);
    }

    public async Task<MasterUserDTO> ReadMasterUserAsync(string masterUserId)
    {
        return await _masterUserService.ReadMasterUserAsync(masterUserId);
    }
}