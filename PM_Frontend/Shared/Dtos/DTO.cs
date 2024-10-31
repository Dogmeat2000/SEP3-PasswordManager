using Newtonsoft.Json;
using Shared.JSONService;

namespace Shared.Dtos
{
    // Base DTO class
    [JsonObject]
    [JsonConverter(typeof(DTOJsonConverter))]  // Custom converter for polymorphic serialization
    public abstract class DTO
    {
        public int? Id { get; set; }

        public DTO(int? id)
        {
            Id = id;
        }

        public DTO() { }
    }
}