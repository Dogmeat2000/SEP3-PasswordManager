using ServiceLayer.Networking;
using ServiceLayer.Services.Cryptography;

namespace ServiceLayer.Services.LoginEntryService;

public class LoginEntryServiceImpl : ILoginEntryService
{
    private readonly ICryptographyService _cryptographyService;
    private readonly IWebApiClient _webApiClient;

    public LoginEntryServiceImpl(ICryptographyService cryptographyService, IWebApiClient webApiClient)
    {
        _cryptographyService = cryptographyService;
        _webApiClient = webApiClient;
    }
}