using Shared.Dtos;

namespace ServiceLayer.Services.Authentication;

public interface IAuthenticationService
{
    Task<MasterUserDTO> AuthenticateUserAsync(string username, string password);
}