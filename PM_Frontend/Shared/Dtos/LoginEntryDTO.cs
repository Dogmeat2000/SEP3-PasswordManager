using Newtonsoft.Json;

namespace Shared.Dtos
{
    public class LoginEntryDTO : DTO
    {
        public string? entryUsername { get; set; }
        public string? entryPassword { get; set; }
        public int? masterUserId { get; set; }

        public LoginEntryDTO(int? id, string? entryUsername, string? entryPassword, int? masterUserId) : base(id)
        {
            this.entryUsername = entryUsername;
            this.entryPassword = entryPassword;
            this.masterUserId = masterUserId;
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