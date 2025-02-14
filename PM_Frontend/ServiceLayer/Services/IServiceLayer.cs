﻿using Shared.CommunicationObjects;
using Shared.Dtos;
using System.Threading.Tasks;

namespace ServiceLayer.Services
{
    /** The interface which the front-end application contacts with requests that will be handled
     * (validated, encrypted, and other business logic) by the service layer.
     */
    public interface IServiceLayer
    {
        /**
         * Sends a create request to create a new MasterUser.
         * Delegates business-logic to IMasterUserService.
         * @param masterUserDto the master user which is requested to be created
         * @return MasterUserDTO: the created master user
         */
        Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto);

        /**
         * WARNING: This should not be accessible as it will return a master user with username and its encrypted password, 
         * and only takes the id as param. Another method should be made for login-purposes.
         * Sends a read request to get a master user.
         * Delegates business-logic to IMasterUserService.
         * @param masterUserId the id of the master user which is wanted
         * @return MasterUserDTO: The master user which id is used as parameter.
         */
        Task<ServerResponse> ReadMasterUserAsync(MasterUserDTO masterUserDto);


        /** <summary>Sends a request to fetch all login_entries, associated with the designated MasterUser.
         * Delegates business logic to ILoginEntryService (Such as validation and encryption).</summary>
         * <param name="dto">A <see cref="MasterUserDTO"/> object that contains the currently logged in MasterUser's id, username and password</param>
         * <returns> <p>If Successful: A <see cref="ServerResponse"/> object containing a <b>decrypted</b> DTO of <see cref="LoginEntryListDTO"/> type, associated with the logged-in user...</p>
         * <p>If unsuccessful: A <see cref="ServerResponse"/> object containing the received HTTP status code and exception message</p></returns>
         * <exception cref="ArgumentException ">Thrown if validation of <see cref="MasterUserDTO"/> failed.
         * Provided MasterUser does not contain the currently logged in users id, username and password.</exception>
         */
        Task<ServerResponse> ReadLoginEntriesAsync(MasterUserDTO dto);

        /**
         * Sends a create request for a new login entry.
         * Delegates business-logic to ILoginEntryService.
         * Handles validation before encryption and encrypts the DTO before it is sent on.
         *
         * @param newEntry LoginEntryDTO containing the data for the new entry
         * @return ServerResponse containing the created LoginEntryDTO
         */
        Task<ServerResponse> CreateLoginEntryAsync(LoginEntryDTO newEntry);

        /**
         * Sends an update request for an existing login entry.
         * Delegates business-logic to ILoginEntryService.
         * Handles validation before encryption and encrypts the DTO before it is sent on.
         *
         * @param updatedEntry LoginEntryDTO containing updated data for the entry
         * @return ServerResponse containing the updated LoginEntryDTO
         */
        Task<LoginEntryDTO> UpdateLoginEntryAsync(LoginEntryDTO updatedEntry);

        /**
         * Sends a delete request for a login entry based on the entry ID.
         * Delegates business-logic to ILoginEntryService.
         *
         * @param entryId The ID of the entry to be deleted
         * @return ServerResponse indicating success or failure of the deletion
         */
        Task<bool> DeleteLoginEntryAsync(LoginEntryDTO entryToDelete);
    }
}