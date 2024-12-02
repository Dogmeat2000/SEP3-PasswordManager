using ServiceLayer.Networking;
using ServiceLayer.Services.Cryptography;
using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.MasterUserService;

/**
 * Service implementation for handling master user operations such as creation and retrieval.
 * Communicates with a web API to perform the required tasks.
 */
public class MasterUserServiceImpl : IMasterUserService
{
    private readonly ICryptographyService _cryptographyService;
    private readonly IWebApiClient _webApiClient;

    public MasterUserServiceImpl(ICryptographyService cryptographyService, IWebApiClient webApiClient)
    {
        _cryptographyService = cryptographyService;
        _webApiClient = webApiClient;
    }

    /**
  * Creates a new master user by sending the provided MasterUserDTO to the web API.
  *
  * @param masterUserDto The data transfer object containing the details of the master user to be created.
  * @return A Task that represents the asynchronous operation. The task result contains a ServerResponse object,
  *         which indicates the result of the create operation.
  * @throws ArgumentNullException if masterUserDto is null.
  */
    public async Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto)
    {
        var returnedServerResponse = await _webApiClient.CreateMasterUserAsync(masterUserDto);
        
        return returnedServerResponse;
    }

    
    /**
  * Reads information about an existing master user by sending the provided MasterUserDTO to the web API.
  *
  * @param masterUserDto The data transfer object containing details for querying the master user.
  * @return A Task that represents the asynchronous operation. The task result contains a ServerResponse object,
  *         which includes the details of the requested master user or an error if the user could not be found.
  * @throws ArgumentNullException if masterUserDto is null.
  */
    public async Task<ServerResponse> ReadMasterUserAsync(MasterUserDTO masterUserDto)
    {
        var returnedMasterUserDto = await _webApiClient.ReadMasterUserAsync(masterUserDto);
        

        return returnedMasterUserDto;
    }
    
}