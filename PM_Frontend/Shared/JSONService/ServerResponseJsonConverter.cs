using System;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Shared.CommunicationObjects;
using Shared.Dtos;

namespace Shared.JSONService
{
    public class ServerResponseJsonConverter : JsonConverter
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
        
        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            // Load the entire JSON object into memory
            var jsonObject = JObject.Load(reader);
            Console.WriteLine("Deserializing ServerResponse: " + jsonObject);

            // Extract statusCode and message directly
            var statusCode = jsonObject["statusCode"]?.Value<int>() ?? throw new JsonSerializationException("Missing statusCode");
            var message = jsonObject["message"]?.Value<string>() ?? string.Empty;

            // Extract the dto based on @class manually
            var dtoToken = jsonObject["dto"];
            DTO dto = null;

            if (dtoToken != null && dtoToken.Type != JTokenType.Null)
            {
                // Manually check @class to determine DTO type
                var typeName = dtoToken["@class"]?.ToString();
                if (typeName != null)
                {
                    // Map @class to actual DTO type
                    if (typeName.Contains("MasterUserDTO"))
                    {
                        dto = dtoToken.ToObject<MasterUserDTO>(); // Deserialize manually to MasterUserDTO
                    }
                    else if (typeName.Contains("LoginEntryDTO"))
                    {
                        dto = dtoToken.ToObject<LoginEntryDTO>(); // Deserialize manually to LoginEntryDTO
                    }
                    else if (typeName.Contains("LoginEntryListDTO"))
                    {
                        dto = dtoToken.ToObject<LoginEntryListDTO>();
                    }
                    else
                    {
                        throw new JsonSerializationException($"Unknown DTO type: {typeName}");
                    }
                }
            }

            // Return the populated ServerResponse object
            return new ServerResponse
            {
                statusCode = statusCode,
                message = message,
                dto = dto
            };
        }
    }
}