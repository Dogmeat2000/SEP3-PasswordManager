namespace Shared.Dtos;

public class LoginEntryListDTO : DTO
{
    public List<LoginEntryDTO>? loginEntries { get; set; }

    public LoginEntryListDTO()
    {
    }

    public LoginEntryListDTO(int? id, List<LoginEntryDTO>? loginEntries) : base(id)
    {
        this.loginEntries = loginEntries;
    }

    public void AddLoginEntry(LoginEntryDTO loginEntry)
    {
        if (loginEntries == null)
        {
            loginEntries = new List<LoginEntryDTO>();
        }
        loginEntries.Add(loginEntry);
    }

    public override string ToString()
    {
        if (loginEntries == null || !loginEntries.Any())
        {
            return "LoginEntryListDTO: No entries available";
        }
        
        var entriesAsString = string.Join(", ", loginEntries.Select(entry => entry.ToString()));
        return $"LoginEntryListDTO: [{entriesAsString}]";
    }
}