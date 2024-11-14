using System.Text.Json.Serialization;
using Newtonsoft.Json;

namespace Shared.Dtos
{
    public class LoginEntryDTO : DTO
    {
            [JsonProperty("entryName")]
            public string? EntryName { get; set; }
    
            [JsonProperty("entryPassword")]
            public string? EntryPassword { get; set; }
    
            [JsonProperty("entryUsername")]
            public string? EntryUsername { get; set; }
    
            [JsonProperty("entryAddress")]
            public string? EntryAddress { get; set; }
    
            [JsonProperty("masterUserId")]
            public int? MasterUserId { get; set; }
    
            [JsonProperty("entryCategory")]
            public string? EntryCategory { get; set; }

            public LoginEntryDTO(int? id, string? entryUsername, string? entryPassword, int? masterUserId, string? entryCategory, string? entryName, string? entryAddress) : base(id)
            {
                EntryName = entryName;
                EntryUsername = entryUsername;
                EntryPassword = entryPassword;
                EntryAddress = entryAddress;
                MasterUserId = masterUserId;
                EntryCategory = entryCategory ?? "Other";
            }
    
            public LoginEntryDTO(int id) : base(id) { }
            public LoginEntryDTO() { }
        
        public override string ToString()
        {
            string toString = "[";
            toString += "Name: " + EntryName;
            toString += " ; Address: ";
            toString += EntryAddress;
            toString += "; Username: ";
            toString += EntryUsername;
            toString += "; Password: ";
            toString += EntryPassword;
            toString += "; Category: ";
            toString += EntryCategory;
            toString += "; MasterUserId: ";
            toString += MasterUserId;
            toString += "]";
            return toString;
        }
    }
}