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
        encryptedMasterUserDto.id = masterUserDto.id;
        
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
                                                                                     
        encryptedLoginEntryDto.EntryUsername = AesEncryptionHelper.Encrypt(loginEntryDto.EntryUsername);
        encryptedLoginEntryDto.EntryPassword = AesEncryptionHelper.Encrypt(loginEntryDto.EntryPassword);
        
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
            AesEncryptionHelper.Decrypt(decryptedLoginEntry.EntryUsername);
            AesEncryptionHelper.Decrypt(decryptedLoginEntry.EntryPassword);
        }
        
        return await Task.FromResult(decryptedServerResponse);
    }
    
    public async Task<ServerResponse> DecryptLoginEntryListAsync(ServerResponse serverResponse)
    {
        ServerResponse decryptedServerResponse = new();
        decryptedServerResponse.message = serverResponse.message;
        decryptedServerResponse.statusCode = serverResponse.statusCode;

        try {
            if (decryptedServerResponse.dto.GetType() == typeof(LoginEntryListDTO)) {

                LoginEntryListDTO receivedLoginEntryListDto = (LoginEntryListDTO)decryptedServerResponse.dto;
                LoginEntryListDTO decryptedLoginEntryListDTO = new();
                foreach (var loginEntryDTO in receivedLoginEntryListDto.loginEntries) {
                    LoginEntryDTO decryptedLoginEntry = (LoginEntryDTO)decryptedServerResponse.dto;
                    decryptedLoginEntry.id = int.Parse(AesEncryptionHelper.Decrypt(loginEntryDTO.id.ToString()));
                    decryptedLoginEntry.EntryUsername = AesEncryptionHelper.Decrypt(loginEntryDTO.EntryUsername);
                    decryptedLoginEntry.EntryPassword = AesEncryptionHelper.Decrypt(loginEntryDTO.EntryPassword);
                    decryptedLoginEntry.EntryAddress = AesEncryptionHelper.Decrypt(loginEntryDTO.EntryAddress);
                    decryptedLoginEntry.EntryName = AesEncryptionHelper.Decrypt(loginEntryDTO.EntryName);
                    decryptedLoginEntry.EntryCategory = AesEncryptionHelper.Decrypt(loginEntryDTO.EntryCategory);
                    decryptedLoginEntryListDTO.AddLoginEntry(decryptedLoginEntry);
                }
            } else {
                throw new ApplicationException();
            }
        } catch (Exception e) {
            throw new ApplicationException("Failed to decrypt the fetched login entries. Reason: " + e.Message);
        }
        
        return await Task.FromResult(decryptedServerResponse);
    }
}