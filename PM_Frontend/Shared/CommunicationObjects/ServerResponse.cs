using Newtonsoft.Json;
using Shared.Dtos;
using Shared.JSONService;

namespace Shared.CommunicationObjects;

[JsonObject]
[JsonConverter(typeof(ServerResponseJsonConverter))]
public class ServerResponse
{
    public ServerResponse()
    {
    }

    public ServerResponse(int statusCode, string message)
    {
        this.statusCode = statusCode;
        this.message = message;
        this.dto = null;
    }

    public ServerResponse(int statusCode, DTO dto)
    {
        this.statusCode = statusCode;
        this.message = null;
        this.dto = dto;
    }

    
    public string? message { get; set; }
    
    
    public int statusCode { get; set; }
    
    
    public DTO? dto { get; set; }
    
    public override string ToString()
    {
        return $"ServerResponse {{ statusCode = {statusCode}, message = \"{message}\", dto = {JsonConvert.SerializeObject(dto, Formatting.Indented)} }}";
    }
}