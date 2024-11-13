using Newtonsoft.Json;

namespace Shared.Dtos
{
    public class LoginEntryDTO : DTO
    {
        public string? EntryName { get; set; }
        public string? EntryUsername { get; set; }
        public string? EntryPassword { get; set; }
        public string? EntryAddress { get; set; }
        public int? MasterUserId { get; set; }
        public string? Category { get; set; }

        public LoginEntryDTO(int? id, string? entryUsername,
            string? entryPassword, int? masterUserId,
            string? Category) : base(id)
        {
            EntryUsername = entryUsername;
            EntryPassword = entryPassword;
            MasterUserId = masterUserId;
            this.Category = Category;
        }
        

        public LoginEntryDTO(int id) : base(id) { }

        public LoginEntryDTO()
        {
        }

        public override string ToString()
        {
            string toString = "[";
            toString += EntryUsername;
            toString += " ; ";
            toString += EntryPassword;
            toString += "]";
            return toString;
        }
    }
}