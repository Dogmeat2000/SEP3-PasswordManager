using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Services.LoginEntryService
{
    /**
     * Handles business logic for the LoginEntry data entity
     * Business logic includes encryption, validation, and communication with the API.
     */
    public interface ILoginEntryService
    {
        /** <summary>Sends a request to fetch all login_entries, associated with the designated MasterUser.
         * Handles business logic, such as validated and encrypted before transmission, and decrypted upon receival. </summary>
         * <param name="dto">A <see cref="MasterUserDTO"/> object that contains the currently logged in MasterUser's id, username and password</param>
         * <returns> <p>If Successful: A <see cref="ServerResponse"/> object containing a <b>decrypted</b> DTO of <see cref="LoginEntryListDTO"/> type, associated with the logged-in user...</p>
         * <p>If unsuccessful: A <see cref="ServerResponse"/> object containing the received HTTP status code and exception message</p></returns>
         * <exception cref="ArgumentException ">Thrown if validation of <see cref="MasterUserDTO"/> failed.
         * Provided MasterUser does not contain the currently logged in users id, username and password.</exception>
         */
        Task<ServerResponse> ReadLoginEntriesAsync(MasterUserDTO dto);

        /**
         * Sends a create-request for a new login entry.
         * Handles validation before encryption.
         * Encrypts the DTO before it is sent on.
         *
         * @param newEntry LoginEntryDTO containing data for the new entry
         * @return ServerResponse containing the created LoginEntryDTO
         */
        Task<ServerResponse> CreateLoginEntryAsync(LoginEntryDTO newEntry);

        /**
         * Sends an update-request for an existing login entry.
         * Handles validation before encryption.
         * Encrypts the DTO before it is sent on.
         *
         * @param updatedEntry LoginEntryDTO containing updated data for the entry
         * @return ServerResponse containing the updated LoginEntryDTO
         */
        Task<LoginEntryDTO> UpdateLoginEntryAsync(LoginEntryDTO updatedEntry);

        /**
         * Sends a delete-request for a login entry based on the entry ID.
         *
         * @param entryId The ID of the entry to be deleted
         * @return ServerResponse indicating success or failure of the deletion
         */
        Task<bool> DeleteLoginEntryAsync(LoginEntryDTO entryToDelete);
    }
}