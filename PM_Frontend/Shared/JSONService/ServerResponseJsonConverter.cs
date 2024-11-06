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

        /*public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            // Load the entire JSON object
            var jsonObject = JObject.Load(reader);
            Console.WriteLine("1"+jsonObject.ToString());
            
            // Extract the statusCode and message fields
            var statusCode = jsonObject["statusCode"]?.Value<int>() ?? throw new JsonSerializationException("Missing statusCode");
            var message = jsonObject["message"]?.Value<string>() ?? string.Empty;

            // Extract the dto field and handle it based on the @class property
            var dtoToken = jsonObject["dto"];
            object dto = null;
            
            if (dtoToken != null)
            {
                // Check for @class in dto to determine the type
                var typeName = dtoToken["@class"]?.ToString();
                if (typeName != null)
                {
                    // Map @class to the actual DTO type
                    if (typeName.Contains("MasterUserDTO"))
                    {
                        typeName = "Shared.Dtos.MasterUserDTO, Shared";
                    }
                    else if (typeName.Contains("LoginEntryDTO"))
                    {
                        typeName = "Shared.Dtos.LoginEntryDTO, Shared";
                    }

                    var dtoType = Type.GetType(typeName);
                    if (dtoType != null)
                    {
                        dto = dtoToken.ToObject(dtoType, serializer);
                    }
                    else
                    {
                        throw new JsonSerializationException($"Unknown DTO type: {typeName}");
                    }
                }
            }

            // Return a populated ServerResponse object
            ServerResponse something = new ServerResponse
            {
                statusCode = statusCode,
                message = message,
                dto = dto as DTO
            };

            Console.WriteLine("something: " + something);
            return something;
        }*/
        
        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            // Load the entire JSON object into memory
            var jsonObject = JObject.Load(reader);
            Console.WriteLine("Deserializing ServerResponse: " + jsonObject.ToString());

            // Extract statusCode and message directly
            var statusCode = jsonObject["statusCode"]?.Value<int>() ?? throw new JsonSerializationException("Missing statusCode");
            var message = jsonObject["message"]?.Value<string>() ?? string.Empty;

            // Extract the dto based on @class manually
            var dtoToken = jsonObject["dto"];
            DTO dto = null;

            if (dtoToken != null)
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