using System.Security.Claims;
using Microsoft.AspNetCore.Components.Authorization;
using ServiceLayer.Services;
using Shared.Dtos;

namespace BlazorUI.Components.Auth;

public class AuthProvider : AuthenticationStateProvider
{
    private readonly IServiceLayer serviceLayer;
    private ClaimsPrincipal currentUser = new(new ClaimsIdentity());

    public AuthProvider(IServiceLayer serviceLayer)
    {
        this.serviceLayer = serviceLayer;
    }

    public override Task<AuthenticationState> GetAuthenticationStateAsync()
    {
        return Task.FromResult(new AuthenticationState(currentUser));
    }

    public async Task Login(string username, string password)
    {
    
        var loginDto = new MasterUserDTO
        {
            masterUsername = username,
            masterPassword = password
        };

        var response = await serviceLayer.ReadMasterUserAsync(loginDto);

        if (response.statusCode == 200)
        {
            var claims = new List<Claim>
            {
                new(ClaimTypes.Name, username),
            };

            var identity = new ClaimsIdentity(claims, "apiauth");
            currentUser = new ClaimsPrincipal(identity);
            NotifyAuthenticationStateChanged(GetAuthenticationStateAsync());
        }
        else
        {
            throw new Exception("Invalid login credentials");
        }
    }

    public void Logout()
    {
        currentUser = new ClaimsPrincipal(new ClaimsIdentity());
        NotifyAuthenticationStateChanged(GetAuthenticationStateAsync());
    }
}