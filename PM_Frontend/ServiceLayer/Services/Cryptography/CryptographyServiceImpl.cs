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

    public async Task<MasterUserDTO> DecryptMasterUserAsync(MasterUserDTO masterUserDto)
    {
        MasterUserDTO decryptedMasterUserDto = new();
        
        decryptedMasterUserDto.masterUsername = AesEncryptionHelper.Decrypt(masterUserDto.masterUsername);
        decryptedMasterUserDto.masterPassword = AesEncryptionHelper.Decrypt(masterUserDto.masterPassword);
        
        return await Task.FromResult(decryptedMasterUserDto);
    }

    public async Task<LoginEntryDTO> EncryptLoginEntryAsync(LoginEntryDTO loginEntryDto)
    {                                                                                
        LoginEntryDTO encryptedLoginEntryDto = new();                                
                                                                                     
        encryptedLoginEntryDto.entryUsername = AesEncryptionHelper.Encrypt(loginEntryDto.entryUsername);
        encryptedLoginEntryDto.entryPassword = AesEncryptionHelper.Encrypt(loginEntryDto.entryPassword);
        
        return await Task.FromResult(encryptedLoginEntryDto);
    }

    public async Task<LoginEntryDTO> DecryptLoginEntryAsync(LoginEntryDTO loginEntryDto)
    {
        LoginEntryDTO decryptedMasterUserDto = new();
        
        decryptedMasterUserDto.entryUsername = AesEncryptionHelper.Decrypt(loginEntryDto.entryUsername);
        decryptedMasterUserDto.entryPassword = AesEncryptionHelper.Decrypt(loginEntryDto.entryPassword);
        
        return await Task.FromResult(decryptedMasterUserDto);
    }
}