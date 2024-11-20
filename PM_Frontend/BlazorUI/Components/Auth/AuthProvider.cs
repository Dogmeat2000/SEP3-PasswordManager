using System.Security.Claims;
using System.Text.Json;
using Microsoft.AspNetCore.Components.Authorization;
using Microsoft.JSInterop;
using ServiceLayer.Services;
using Shared.Dtos;

namespace BlazorUI.Components.Auth;

public class AuthProvider : AuthenticationStateProvider
{
    private readonly IServiceLayer serviceLayer;
    private readonly IJSRuntime jsRuntime;

    public AuthProvider(IServiceLayer serviceLayer, IJSRuntime jsRuntime)
    {
        this.serviceLayer = serviceLayer;
        this.jsRuntime = jsRuntime;
    }

    public async Task Login(MasterUserDTO masterUserDto)
    {
       var serverResponse = await serviceLayer.ReadMasterUserAsync(masterUserDto);

       if (serverResponse.statusCode != 200)
       {
           throw new Exception(serverResponse.statusCode.ToString());
       }

       MasterUserDTO masterUser = (MasterUserDTO) serverResponse.dto;
       string jsonSerializedMasterUser = JsonSerializer.Serialize(masterUser);
       await jsRuntime.InvokeVoidAsync("sessionStorage.setItem", "currentUser", jsonSerializedMasterUser);

       List<Claim> claims = new List<Claim>()
       {
           new Claim(ClaimTypes.Name, masterUser.masterUsername)
       };
       
       ClaimsIdentity identity = new ClaimsIdentity(claims, "apiauth");
       ClaimsPrincipal claimsPrincipal = new ClaimsPrincipal(identity);
       
       NotifyAuthenticationStateChanged(
           Task.FromResult(new AuthenticationState(claimsPrincipal))
       );
    }
    
    public override async Task<AuthenticationState> GetAuthenticationStateAsync()
    {
        string userAsJson = "";
        try
        {
            userAsJson = await jsRuntime.InvokeAsync<string>("sessionStorage.getItem", "currentUser");
        }
        catch (InvalidOperationException e)
        {
            return new AuthenticationState(new());
        }

        if (string.IsNullOrEmpty(userAsJson))
        {
            return new AuthenticationState(new());
        }
        
        MasterUserDTO masterUserDto = JsonSerializer.Deserialize<MasterUserDTO>(userAsJson);
        List<Claim> claims = new List<Claim>()
        {
            new Claim(ClaimTypes.Name, masterUserDto.masterUsername)
        };
        ClaimsIdentity identity = new ClaimsIdentity(claims, "apiauth");
        ClaimsPrincipal claimsPrincipal = new ClaimsPrincipal(identity);
        return new AuthenticationState(claimsPrincipal);
    }











    /*
       public override Task<AuthenticationState> GetAuthenticationStateAsync()
       {
           return Task.FromResult(new AuthenticationState(currentUser));
       } */

    /*
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
    } */

}