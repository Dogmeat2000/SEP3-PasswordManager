using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.Cryptography;

public interface ICryptographyService
{
    Task<MasterUserDTO> EncryptMasterUserAsync(MasterUserDTO masterUserDTO);
    Task<ServerResponse> DecryptServerResponceAsync(ServerResponse serverResponse);
    Task<LoginEntryDTO> EncryptLoginEntryAsync(LoginEntryDTO loginEntryDTO);
    Task<ServerResponse> DecryptLoginEntryAsync(ServerResponse serverResponse);
    Task<ServerResponse> DecryptLoginEntryListAsync(ServerResponse serverResponse);
}