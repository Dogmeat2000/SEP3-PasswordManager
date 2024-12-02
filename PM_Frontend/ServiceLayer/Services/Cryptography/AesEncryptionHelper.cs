using System.Collections;
using System.Security.Cryptography;
using System.Text;

namespace ServiceLayer.Services.Cryptography;

/** AES encryption helper class.
 * Uses symmetrical encryption AES (Advanced Encryption Standard) for encryption of sensitive data.
 * The same key is used both for encryption and decryption *
 */ 
public class AesEncryptionHelper
{
    private static readonly byte[] Key = Encoding.UTF8.GetBytes("1234567890123456"); // In future, change to more secure key

    public static string Encrypt(string textToEncrypt)
    {
        using (Aes aes = Aes.Create())
        {
            aes.Key = Key;
            aes.GenerateIV();
            
            ICryptoTransform encryptor = aes.CreateEncryptor(aes.Key, aes.IV);

            using (MemoryStream memoryStream = new MemoryStream())
            {
                memoryStream.Write(aes.IV, 0, aes.IV.Length);
                
                using (CryptoStream cryptoStream = new CryptoStream(memoryStream, encryptor, CryptoStreamMode.Write))
                {
                    using (StreamWriter streamWriter = new StreamWriter(cryptoStream))
                    {
                        streamWriter.Write(textToEncrypt);
                    }
                    
                    return Convert.ToBase64String(memoryStream.ToArray());
                }
            }
        }
    }

    public static string Decrypt(string textToDecrypt)
    {
        byte[] fullCipher = Convert.FromBase64String(textToDecrypt);
        
        using (Aes aes = Aes.Create())
        {
            aes.Key = Key;

            byte[] iv = new byte[aes.BlockSize / 8]; 
            Array.Copy(fullCipher, iv, iv.Length);
            aes.IV = iv;

            using (MemoryStream memoryStream = new MemoryStream(fullCipher, iv.Length, fullCipher.Length - iv.Length))
            {
                ICryptoTransform decryptor = aes.CreateDecryptor(aes.Key, aes.IV);
                using (CryptoStream cryptoStream = new CryptoStream(memoryStream, decryptor, CryptoStreamMode.Read))
                {
                    using (StreamReader streamReader = new StreamReader(cryptoStream))
                    {
                        return streamReader.ReadToEnd();
                    }
                }
            }
        }
    }
}