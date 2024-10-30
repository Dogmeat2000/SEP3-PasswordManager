using Shared.Dtos;

namespace ServiceLayer.Services;

/** The interface which the front-end application contacts with requests that will be handled (Validated, encrypted and other business logic) by the service layer.
 */
public interface IServiceLayer
{
 /**
     * Sends a create request to create a new MasterUser.
     * Delegates business-logic to IMasterUserService
     * @param masterUserDto the master user which is requested to be created
     * @return MasterUserDTO: the created master user
     */
 Task<MasterUserDTO> CreateMasterUserAsync(MasterUserDTO masterUserDto);

 /**
     * WARNING: This should not be accessible as it will return a master user with username and password, and only takes the id as param.
     * Another method should be made for login-purposes.
     * Sends a read request to get a master user
     * Delegates business-logic to IMasterUserService
     * @param masterUserId the id of the master user which is wanted
     * @return MasterUserDTO: The master user which id is used as parameter.
     * Todo: Think about if parameters should be changed so that the method can be used for login-purposes.
     */
 Task<MasterUserDTO> ReadMasterUserAsync(int masterUserId);
}