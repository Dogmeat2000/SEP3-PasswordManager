﻿using Shared.CommunicationObjects;
using Shared.Dtos;

namespace ServiceLayer.Networking;

/**
 * Handles the communication with the Web-api, sends and receives requests.
 */
public interface IWebApiClient
{
    Task<ServerResponse> CreateMasterUserAsync(MasterUserDTO masterUserDto);

    Task<ServerResponse> ReadMasterUserAsync(int masterUserId);
    Task<ServerResponse> CreateLoginEntryAsync(LoginEntryDTO encryptLoginEntryAsync);
    Task<ServerResponse> UpdateLoginEntryAsync(LoginEntryDTO encryptLoginEntryAsync);
    Task<ServerResponse> DeleteLoginEntryAsync(LoginEntryDTO entryToDelete);
}