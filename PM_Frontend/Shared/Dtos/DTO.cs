using Newtonsoft.Json;
using Shared.JSONService;

namespace Shared.Dtos
{
    // Base DTO class
    [JsonObject]
    [JsonConverter(typeof(DTOJsonConverter))]  // Custom converter for polymorphic serialization
    public abstract class DTO
    {
        public int? id { get; set; }

        public DTO(int? id)
        {
            this.id = id;
        }

        public DTO() { }

        public abstract override string ToString();
    }
}