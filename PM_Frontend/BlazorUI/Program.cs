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
            
            // TODO: Marcus, commented out due to errors.
            /*builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                .AddJwtBearer(options =>
                {
                    options.TokenValidationParameters = new TokenValidationParameters
                    {
                        ValidateIssuer = true,
                        ValidateAudience = true,
                        ValidateLifetime = true,
                        ValidateIssuerSigningKey = true,
                        ValidIssuer = "yourdomain.com", 
                        ValidAudience = "yourdomain.com", 
                        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("your_secret_key")) 
                    };
                });*/
            
            //builder.Services.AddAuthorizationCore();

            var app = builder.Build();
            
            if (!app.Environment.IsDevelopment())
            {
                app.UseExceptionHandler("/Error");
                app.UseHsts(); 
            }
            
    
            app.UseHttpsRedirection();
            
         
            app.UseStaticFiles();
            app.UseAntiforgery();
            
       
            // TODO: Marcus, commented out due to errors.
            /*app.UseRouting();
            app.UseAuthentication();
            app.UseAuthorization(); */

       
            app.MapRazorComponents<App>()
                .AddInteractiveServerRenderMode();
            
    
            app.Run();
        }
    }
}
