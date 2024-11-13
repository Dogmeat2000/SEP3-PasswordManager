using Newtonsoft.Json;

namespace Shared.Dtos
{
    public class LoginEntryDTO : DTO
    {
        public string? entryName { get; set; }
        public string? entryUsername { get; set; }
        public string? entryPassword { get; set; }
        public string? entryAddress { get; set; }
        public int? masterUserId { get; set; }
        public string? entryCategory { get; set; }

        public LoginEntryDTO(int? id, string? entryUsername,
            string? entryPassword, int? masterUserId,
            string? entryCategory,
            string? entryName,
            string? entryAddress) : base(id)
        {
            this.entryUsername = entryUsername ?? "Error: Unspecified";
            this.entryPassword = entryPassword ?? "Error: Unspecified";
            this.masterUserId = masterUserId;
            this.entryCategory = entryCategory ?? "Other";
            this.entryName = entryName ?? "Error: Unspecified";
            this.entryAddress = entryAddress ?? "Error: Unspecified";
        }
        

        public LoginEntryDTO(int id) : base(id) { }

        public LoginEntryDTO()
        {
        }

        public override string ToString()
        {
            string toString = "[";
            toString += "Name: " + entryName;
            toString += " ; Address: ";
            toString += entryAddress;
            toString += "; Username: ";
            toString += entryUsername;
            toString += "; Password: ";
            toString += entryPassword;
            toString += "; Category: ";
            toString += entryCategory;
            toString += "; MasterUserId: ";
            toString += masterUserId;
            toString += "]";
            return toString;
        }
    }
}