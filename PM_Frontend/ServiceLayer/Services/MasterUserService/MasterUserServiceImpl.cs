﻿using ServiceLayer.Networking;
using ServiceLayer.Services.Cryptography;
using Shared.Dtos;

namespace ServiceLayer.Services.MasterUserService;

public class MasterUserServiceImpl : IMasterUserService
{
    private readonly ICryptographyService _cryptographyService;
    private readonly IWebApiClient _webApiClient;

    public MasterUserServiceImpl(ICryptographyService cryptographyService, IWebApiClient webApiClient)
    {
        _cryptographyService = cryptographyService;
        _webApiClient = webApiClient;
    }

    public Task<MasterUserDTO> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        throw new NotImplementedException();
    }

    public Task<MasterUserDTO> ReadMasterUserAsync(string masterUserId)
    {
        throw new NotImplementedException();
    }
}