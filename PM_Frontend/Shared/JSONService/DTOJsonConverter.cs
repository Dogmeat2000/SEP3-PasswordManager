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

        /*public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            // Load the entire JSON object
            var jsonObject = JObject.Load(reader);
            Console.WriteLine("2"+jsonObject.ToString());
            

            // Extract the dto field and handle it based on the @class property
            //var dtoToken = jsonObject["dto"];
            object dto = null;

            if (dtoToken != null)
            {
                // Check for @class in dto to determine the type
                //var typeName = dtoToken["@class"]?.ToString();
                var typeName = jsonObject["@class"]?.ToString();
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
                        dto = jsonObject.ToObject(dtoType, serializer);
                    }
                    else
                    {
                        throw new JsonSerializationException($"Unknown DTO type: {typeName}");
                    }
                }
            //}

            Console.WriteLine("dto is: " + dto);
            // Return a populated ServerResponse object
            return dto;
        }*/

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
                else if (typeName.Contains("LoginEntryDTO"))
                {
                    return jsonObject.ToObject<LoginEntryDTO>(); // Deserialize directly to LoginEntryDTO
                }
            }

            throw new JsonSerializationException("Missing or unknown @class property in DTO.");
            
        }
}