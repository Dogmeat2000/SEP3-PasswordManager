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
        /**
         * Sends a request to retrieve all login entries.
         * Decrypts the passwords in the returned entries before returning them.
         *
         * @return ServerResponse containing the list of LoginEntryDTO objects
         */
        Task<ServerResponse> ReadLoginEntriesAsync();

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