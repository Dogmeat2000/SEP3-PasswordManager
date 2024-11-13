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
        public string? Category { get; set; }

        public LoginEntryDTO(int? id, string? entryUsername,
            string? entryPassword, int? masterUserId,
            string? entryCategory,
            string? entryName,
            string? entryAddress) : base(id)
        {
            this.entryUsername = entryUsername;
            this.entryPassword = entryPassword;
            this.masterUserId = masterUserId;
            this.Category = Category;
        }
        

        public LoginEntryDTO(int id) : base(id) { }

        public LoginEntryDTO()
        {
        }

        public override string ToString()
        {
            string toString = "[";
            toString += entryUsername;
            toString += " ; ";
            toString += entryPassword;
            toString += "]";
            return toString;
        }
    }
}