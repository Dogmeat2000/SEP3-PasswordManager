using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Shared.Dtos;

namespace Shared.JSONService
{
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

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            var jsonObject = JObject.Load(reader);
            var typeName = jsonObject["@class"]?.ToString();
            Console.WriteLine(jsonObject.ToString());
            if (typeName.Contains("MasterUserDTO"))
            {
                typeName = "Shared.Dtos.MasterUserDTO, Shared";
            }
            
            else if (typeName.Contains("LoginEntryDTO"))
            {
                typeName = "Shared.Dtos.LoginEntryDTO, Shared";
            }
            
            var dtoType = Type.GetType(typeName);
            
            Console.WriteLine(dtoType);

            if (dtoType == null)
                throw new JsonSerializationException($"Unknown type: {typeName}");

            return null;

        }
    }
}