using System;
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
                writer.WritePropertyName(prop.Name);
                serializer.Serialize(writer, prop.GetValue(dto));
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
                        entryUsername = jsonObject["entryUsername"]?.ToString(),
                        entryPassword = jsonObject["entryPassword"]?.ToString(),
                        masterUserId = (int?)jsonObject["masterUserId"],
                        id = (int?)jsonObject["id"]
                    };
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