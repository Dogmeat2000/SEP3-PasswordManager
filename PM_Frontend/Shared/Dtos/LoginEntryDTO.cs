namespace Shared.Dtos;

public class LoginEntryDTO : DTO
{
    public LoginEntryDTO(int? id, string? entryUsername, string? entryPassword, int? masterUserId) : base(id)
    {
        this.entryUsername = entryUsername;
        this.entryPassword = entryPassword;
        this.masterUserId = masterUserId;
    }

    public LoginEntryDTO(int id) : base(id)
    {
    }

    public string? entryUsername { get; set; }
    public string? entryPassword { get; set; }
    public int? masterUserId { get; set; }
}