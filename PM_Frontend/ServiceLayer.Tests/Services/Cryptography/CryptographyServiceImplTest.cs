using System;
using JetBrains.Annotations;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using ServiceLayer.Services.Cryptography;
using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Tests.Services.Cryptography;

[TestClass]
[TestSubject(typeof(CryptographyServiceImpl))]
public class CryptographyServiceImplTest
{

    [TestMethod]
    public void Password_ShouldBeEncrypted()
    {
        // Arrange
        LoginEntryDTO testLogin = new(); 
        testLogin.EntryPassword = "password123";
        var cryptographyService = new CryptographyServiceImpl();

        var testLoginClone = new LoginEntryDTO
        {
            id = testLogin.id,
            EntryAddress = testLogin.EntryAddress,
            EntryName = testLogin.EntryName,
            EntryCategory = testLogin.EntryCategory,
            EntryUsername = testLogin.EntryUsername,
            EntryPassword = testLogin.EntryPassword
        };

        // Act
        var encryptedLogin = cryptographyService.EncryptLoginEntryAsync(testLoginClone).Result;
        
        // Assert
        Assert.AreNotEqual(testLogin.EntryPassword, encryptedLogin.EntryPassword, "The encrypted password should not match the original password.");
        Assert.IsNotNull(encryptedLogin.EntryPassword, "The encrypted password should not be null.");
    }

    [TestMethod]
    public void Password_ShouldBeDecrypted()
    {
        // Arrange
        LoginEntryDTO testLogin = new();
        testLogin.EntryPassword = "password123";
        var cryptographyService = new CryptographyServiceImpl();
        var encryptedLogin = cryptographyService.EncryptLoginEntryAsync(testLogin).Result;
        
        // Act
        var serverResponse = new ServerResponse
        {
            message = "Success",
            statusCode = 200,
            dto = encryptedLogin
        };

        var decryptedServerResponse = cryptographyService.DecryptLoginEntryAsync(serverResponse).Result;
        var decryptedLogin = (LoginEntryDTO)decryptedServerResponse.dto;

        // Assert 
        Assert.AreEqual(testLogin.EntryPassword, decryptedLogin.EntryPassword, 
            "The decrypted password should match the original password.");
        Assert.IsNotNull(decryptedLogin.EntryPassword, "The decrypted password should not be null.");
    }
    
}