using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.Cryptography;

public class CryptographyServiceImpl : ICryptographyService
{
    public async Task<MasterUserDTO> EncryptMasterUserAsync(MasterUserDTO masterUserDTO)
    {
        // TODO: Implement encryption logic
        // For now, simply returning the input
        return await Task.FromResult(masterUserDTO);
    }

    public async Task<ServerResponse> DecryptServerResponceAsync(ServerResponse serverResponse)
    {
        // TODO: Implement decryption logic
        // For now, simply returning the input
        return await Task.FromResult(serverResponse);
    }

    public async Task<LoginEntryDTO> EncryptLoginEntryAsync(LoginEntryDTO loginEntryDTO)
    {
        throw new NotImplementedException();
    }

    public async Task<LoginEntryDTO> DecryptLoginEntryAsync(LoginEntryDTO loginEntryDTO)
    {
        throw new NotImplementedException();
    }
}