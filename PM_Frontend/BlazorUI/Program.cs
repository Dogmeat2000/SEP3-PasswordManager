using BlazorUI.Components;
using BlazorUI.Components.Auth;
using ServiceLayer.Factories;
using Microsoft.AspNetCore.Components.Authorization;

namespace BlazorUI
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);
            
            builder.Services.AddRazorComponents()
                .AddInteractiveServerComponents();
            builder.Services.AddAuthorizationCore();
            builder.Services.AddScoped<AuthenticationStateProvider, AuthProvider>();
            
            
            ServiceLayerFactory.RegisterServices(builder.Services, builder.Configuration);
  
            var app = builder.Build();
            
            if (!app.Environment.IsDevelopment())
            {
                app.UseExceptionHandler("/Error");
                app.UseHsts(); 
            }
    
            app.UseHttpsRedirection();
         
            app.UseStaticFiles();
            app.UseAntiforgery();
            

       
            app.MapRazorComponents<App>()
                .AddInteractiveServerRenderMode();
            
    
            app.Run();
        }
    }
}
