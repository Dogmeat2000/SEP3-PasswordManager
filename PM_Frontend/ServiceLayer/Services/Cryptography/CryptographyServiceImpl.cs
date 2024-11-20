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
        LoginEntryDTO encryptedLoginEntryDto = loginEntryDto;                     
                                                                                     
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
            
            Console.WriteLine("From cryp: " + decryptedLoginEntry.EntryName);
            
            AesEncryptionHelper.Decrypt(decryptedLoginEntry.EntryUsername);
            AesEncryptionHelper.Decrypt(decryptedLoginEntry.EntryPassword);
            
        }
        
        return serverResponse; //TODO: Use decryption, as of now it just returns the given serverResponse
    }
    
    public async Task<ServerResponse> DecryptLoginEntryListAsync(ServerResponse serverResponse)
    {
        ServerResponse decryptedServerResponse = new();
        decryptedServerResponse.message = serverResponse.message;
        decryptedServerResponse.statusCode = serverResponse.statusCode;
        decryptedServerResponse.dto = new LoginEntryListDTO();
        LoginEntryListDTO decryptedLoginEntryListDTO = new();

        if (serverResponse == null || serverResponse.dto == null) {
            throw new ApplicationException("ServerResponse is null");
        }
        
        try {
            if (serverResponse.dto.GetType() == typeof(LoginEntryListDTO)) {

                LoginEntryListDTO receivedLoginEntryListDto = (LoginEntryListDTO) serverResponse.dto;
                foreach (var loginEntryDTO in receivedLoginEntryListDto.loginEntries) {
                    LoginEntryDTO decryptedLoginEntry = new LoginEntryDTO();
                    
                    decryptedLoginEntry.id = loginEntryDTO.id;
                    decryptedLoginEntry.EntryAddress = loginEntryDTO.EntryAddress;
                    decryptedLoginEntry.EntryName = loginEntryDTO.EntryName;
                    decryptedLoginEntry.EntryCategory = loginEntryDTO.EntryCategory;

                    try {
                        decryptedLoginEntry.EntryUsername = AesEncryptionHelper.Decrypt(loginEntryDTO.EntryUsername);
                        decryptedLoginEntry.EntryPassword = AesEncryptionHelper.Decrypt(loginEntryDTO.EntryPassword);

                    } catch (FormatException e) {
                        // Catch unencrypted keys. TODO: Delete this try-catch once no non-encrypted keys remain in the test data.
                        decryptedLoginEntry.EntryUsername = loginEntryDTO.EntryUsername;
                        decryptedLoginEntry.EntryPassword = loginEntryDTO.EntryPassword;
                    } catch (ArgumentException e) {
                        // Catch unencrypted keys. TODO: Delete this try-catch once no non-encrypted keys remain in the test data.
                        decryptedLoginEntry.EntryUsername = loginEntryDTO.EntryUsername;
                        decryptedLoginEntry.EntryPassword = loginEntryDTO.EntryPassword;
                    }
                    decryptedLoginEntryListDTO.AddLoginEntry(decryptedLoginEntry);
                }
            } else {
                throw new ApplicationException();
            }
        } 
        catch (Exception e) {
            throw new ApplicationException("Failed to decrypt the fetched login entries. Reason: " + e.Message);
        }

        decryptedServerResponse.dto = decryptedLoginEntryListDTO;
        
        return await Task.FromResult(decryptedServerResponse);
    }
}