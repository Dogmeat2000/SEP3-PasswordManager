namespace Shared.Dtos;

public class MasterUserDTO : DTO
{
    public MasterUserDTO(int? id, string? masterUsername, string? masterPassword) : base(id)
    {
        this.masterUsername = masterUsername;
        this.masterPassword = masterPassword;
    }

    public MasterUserDTO()
    {
    }

    public string? masterUsername { get; set; }
    public string? masterPassword { get; set; }
}