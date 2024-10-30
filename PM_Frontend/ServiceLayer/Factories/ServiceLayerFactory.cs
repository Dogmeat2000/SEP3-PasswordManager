using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Configuration;
using ServiceLayer.Networking;
using ServiceLayer.Services;
using ServiceLayer.Services.Cryptography;
using ServiceLayer.Services.LoginEntryService;
using ServiceLayer.Services.MasterUserService;

namespace ServiceLayer.Factories;

public static class ServiceLayerFactory
{
    public static void RegisterServices(IServiceCollection services, IConfiguration configuration)
    {
        // Register HttpClient with a base address
        services.AddScoped(sp => new HttpClient { BaseAddress = new Uri(configuration["ApiSettings:BaseAddress"]) });
        
        var loadBalancerUrl = configuration["ApiSettings:LoadBalancerUrl"];
        // Register the Crypto Service as Singleton (long-lived and stateless)
        services.AddSingleton<ICryptographyService, CryptographyServiceImpl>();

        // Register API Client with HttpClient and Transient scope (fresh instance per use)
        services.AddScoped<IWebApiClient>(provider =>
            new WebApiClientImpl(provider.GetRequiredService<HttpClient>(), loadBalancerUrl));

        // Register scoped services for Login and Master User functionality
        services.AddScoped<ILoginEntryService, LoginEntryServiceImpl>();
        services.AddScoped<IMasterUserService, MasterUserServiceImpl>();

        // Register the main ServiceLayer implementation, which relies on other services
        services.AddScoped<IServiceLayer, ServiceLayerImpl>();
    }
}