﻿using Newtonsoft.Json;

namespace Shared.Dtos
{
    public class LoginEntryDTO : DTO
    {
        public string? EntryName { get; set; }
        public string? EntryUsername { get; set; }
        public string? EntryPassword { get; set; }
        public string? EntryAddress { get; set; }
        public int? MasterUserId { get; set; }
        public string? EntryCategory { get; set; }

        public LoginEntryDTO(int? id, string? entryUsername,
            string? entryPassword, int? masterUserId,
            string? entryCategory,
            string? entryName, 
            string? entryAddress) : base(id)
        {
            EntryUsername = entryUsername ?? "Error: Unspecified";
            EntryPassword = entryPassword ?? "Error: Unspecified";
            MasterUserId = masterUserId;
            EntryCategory = entryCategory ?? "Other";
            EntryName = entryName ?? "Error: Unspecified";
            EntryAddress = entryAddress ?? "Error: Unspecified";
        }
        

        public LoginEntryDTO(int id) : base(id) { }

        public LoginEntryDTO() { }

        public override string ToString() {
            string toString = "[";
            toString += "Name: " + EntryName;
            toString += " ; Address: ";
            toString += EntryAddress;
            toString += "; Username: ";
            toString += EntryUsername;
            toString += "; Password: ";
            toString += EntryPassword;
            toString += "; Category: ";
            toString += EntryCategory;
            toString += "; MasterUserId: ";
            toString += MasterUserId;
            toString += "]";
            return toString;
        }
    }
}