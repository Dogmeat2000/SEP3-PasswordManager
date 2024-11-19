using BlazorUI.Components;
//using Microsoft.AspNetCore.Authentication.JwtBearer;
//using Microsoft.IdentityModel.Tokens;
using ServiceLayer.Factories;
using System.Text;

namespace BlazorUI
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);
            
            builder.Services.AddRazorComponents()
                .AddInteractiveServerComponents();
            
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
