using Newtonsoft.Json;

namespace Shared.Dtos
{
    public class MasterUserDTO : DTO
    {
        public string? masterUsername { get; set; }
        public string? masterPassword { get; set; }

        public MasterUserDTO(int? id, string? masterUsername, string? masterPassword) : base(id)
        {
            this.masterUsername = masterUsername;
            this.masterPassword = masterPassword;
        }

        public MasterUserDTO() { }

        public override string ToString()
        {
            string toString = "[";
            toString += masterUsername;
            toString += " ; ";
            toString += masterPassword;
            toString += "]";
            return toString;
        }
    }
}