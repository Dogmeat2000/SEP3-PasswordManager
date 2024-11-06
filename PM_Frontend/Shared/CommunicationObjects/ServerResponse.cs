using Newtonsoft.Json;
using Shared.Dtos;
using Shared.JSONService;

namespace Shared.CommunicationObjects;

[JsonObject]
[JsonConverter(typeof(ServerResponseJsonConverter))]
public class ServerResponse
{
    // Parameterless constructor required for deserialization
    public ServerResponse()
    {
    }

    public ServerResponse(int statusCode, string message)
    {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ServerResponse(int statusCode, DTO dto)
    {
        this.statusCode = statusCode;
        this.dto = dto;
    }

    
    public string message { get; set; }
    
    
    public int statusCode { get; set; }
    
    
    public DTO dto { get; set; }
    
    public override string ToString()
    {
        return $"ServerResponse {{ statusCode = {statusCode}, message = \"{message}\", dto = {JsonConvert.SerializeObject(dto, Formatting.Indented)} }}";
    }
}