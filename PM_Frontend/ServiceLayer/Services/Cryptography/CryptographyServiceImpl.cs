using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.Cryptography;

public class CryptographyServiceImpl : ICryptographyService
{
    public async Task<MasterUserDTO> EncryptMasterUserAsync(MasterUserDTO masterUserDto)
    {
        MasterUserDTO encryptedMasterUserDto = new();
        
        encryptedMasterUserDto.masterUsername = AesEncryptionHelper.Encrypt(masterUserDto.masterUsername);
        encryptedMasterUserDto.masterPassword = AesEncryptionHelper.Encrypt(masterUserDto.masterPassword);
        
        return await Task.FromResult(encryptedMasterUserDto);
    }

    public async Task<ServerResponse> DecryptServerResponceAsync(ServerResponse serverResponse)
    {
        ServerResponse decryptedServerResponse = new();
        decryptedServerResponse.message = serverResponse.message;
        decryptedServerResponse.statusCode = serverResponse.statusCode;

        if (decryptedServerResponse.dto.GetType() == typeof(MasterUserDTO))
        {
            MasterUserDTO decryptedMasterUserDto = (MasterUserDTO)decryptedServerResponse.dto;
            AesEncryptionHelper.Decrypt(decryptedMasterUserDto.masterUsername);
            AesEncryptionHelper.Decrypt(decryptedMasterUserDto.masterPassword);
        }
        
        return await Task.FromResult(decryptedServerResponse);
    }

    public async Task<LoginEntryDTO> EncryptLoginEntryAsync(LoginEntryDTO loginEntryDto)
    {                                                                                
        LoginEntryDTO encryptedLoginEntryDto = new();                                
                                                                                     
        encryptedLoginEntryDto.entryUsername = AesEncryptionHelper.Encrypt(loginEntryDto.entryUsername);
        encryptedLoginEntryDto.entryPassword = AesEncryptionHelper.Encrypt(loginEntryDto.entryPassword);
        
        return await Task.FromResult(encryptedLoginEntryDto);
    }

    public async Task<ServerResponse> DecryptLoginEntryAsync(ServerResponse serverResponse)
    {
        ServerResponse decryptedServerResponse = new();
        decryptedServerResponse.message = serverResponse.message;
        decryptedServerResponse.statusCode = serverResponse.statusCode;

        if (decryptedServerResponse.dto.GetType() == typeof(LoginEntryDTO))
        {
            LoginEntryDTO decryptedLoginEntry = (LoginEntryDTO)decryptedServerResponse.dto;
            AesEncryptionHelper.Decrypt(decryptedLoginEntry.entryUsername);
            AesEncryptionHelper.Decrypt(decryptedLoginEntry.entryPassword);
        }
        
        return await Task.FromResult(decryptedServerResponse);
    }
}