using System;
using System.Security.AccessControl;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Shared.CommunicationObjects;
using Shared.Dtos;

namespace Shared.JSONService;

public class DTOJsonConverter : JsonConverter
{
    public override bool CanConvert(Type objectType)
        {
            return typeof(DTO).IsAssignableFrom(objectType);
        }

        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
            var dto = (DTO)value;
            var type = dto.GetType();

            // Begin writing JSON object with type information
            writer.WriteStartObject();
            writer.WritePropertyName("@type");
            writer.WriteValue("common.dto."+type.Name); // Adds type metadata, e.g., "Shared.Dtos.MasterUserDTO"
            
            writer.WritePropertyName("@class");
            writer.WriteValue("common.dto."+type.Name);

            // Write all properties of the DTO
            foreach (var prop in type.GetProperties())
            {
                if (prop.CanRead)
                {
                    var propertyValue = prop.GetValue(dto);
                    if (propertyValue != null)
                    {
                        // Retrieve the JSON property name if specified
                        var jsonPropertyAttribute = prop.GetCustomAttributes(typeof(JsonPropertyAttribute), true)
                            .FirstOrDefault() as JsonPropertyAttribute;
                        var jsonPropertyName = jsonPropertyAttribute != null ? jsonPropertyAttribute.PropertyName : prop.Name;

                        // Write JSON property name and value
                        writer.WritePropertyName(jsonPropertyName);
                        serializer.Serialize(writer, propertyValue);
                    }
                }
            }

            writer.WriteEndObject();
        }

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue,
            JsonSerializer serializer)
        {
            var jsonObject = JObject.Load(reader);
            Console.WriteLine("Deserializing DTO: " + jsonObject.ToString());

            // Determine the type based on @class
            var typeName = jsonObject["@class"]?.ToString();
            if (typeName != null)
            {
                if (typeName.Contains("MasterUserDTO"))
                {
                    MasterUserDTO dto = new MasterUserDTO
                    {
                        masterUsername = jsonObject["masterUsername"]?.ToString(),
                        masterPassword = jsonObject["masterPassword"]?.ToString(),
                        id = (int?)jsonObject["id"]
                    };
                    return dto;
                }
                if (typeName.Contains("LoginEntryDTO"))
                {
                    LoginEntryDTO dto = new LoginEntryDTO
                    {
                        EntryName = jsonObject["entryName"]?.ToString(),
                        EntryUsername = jsonObject["entryUsername"]?.ToString(),
                        EntryPassword = jsonObject["entryPassword"]?.ToString(),
                        EntryAddress = jsonObject["entryAddress"]?.ToString(),
                        MasterUserId = (int?)jsonObject["masterUserId"],
                        EntryCategory = jsonObject["category"]?.ToString(),
                        id = (int?)jsonObject["id"]
                    };
                    
                    Console.WriteLine("From DTOJsonConverter: " + dto.ToString());
                    
                    return dto;
                }

                if (typeName.Contains("LoginEntryListDTO"))
                {
                    // Initialize the LoginEntryListDTO
                    var loginEntryListDTO = new LoginEntryListDTO
                    {
                        id = (int?)jsonObject["id"]
                    };

                    // Deserialize the loginEntries array
                    var entriesArray = jsonObject["loginEntries"] as JArray;
                    if (entriesArray != null)
                    {
                        loginEntryListDTO.loginEntries = entriesArray
                            .Select(entry => entry.ToObject<LoginEntryDTO>(serializer))
                            .ToList();
                    }

                    return loginEntryListDTO;
                }
            }

            throw new JsonSerializationException("Missing or unknown @class property in DTO.");
            
        }
}