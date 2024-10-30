using Shared.Dtos;

namespace ServiceLayer.Services.Cryptography;

public interface ICryptographyService
{
    Task<MasterUserDTO> EncryptMasterUserAsync(MasterUserDTO masterUserDTO);
    Task<MasterUserDTO> DecryptMasterUserAsync(MasterUserDTO masterUserDTO);
    Task<LoginEntryDTO> EncryptLoginEntryAsync(LoginEntryDTO loginEntryDTO);
    Task<LoginEntryDTO> DecryptLoginEntryAsync(LoginEntryDTO loginEntryDTO);
}