
public class MasterUserDTO
{
    public string MasterUsername { get; set; }
    public string MasterPassword { get; set; }
}
    //Common module -> java -> dto
public MasterUserDTO(string username, string password)
{
    MasterUsername = username;
    MasterPassword = password;
}