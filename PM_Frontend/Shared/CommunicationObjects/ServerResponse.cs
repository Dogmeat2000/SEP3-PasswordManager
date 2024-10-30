using Shared.Dtos;

namespace Shared.CommunicationObjects;

public class ServerResponse
{
    // Parameterless constructor required for deserialization
    public ServerResponse()
    {
    }

    public ServerResponse(int statusCode, string message)
    {
        StatusCode = statusCode;
        Message = message;
    }

    public ServerResponse(int statusCode, DTO dto)
    {
        StatusCode = statusCode;
        Dto = dto;
    }

    public string Message { get; set; }
    public int StatusCode { get; set; }
    public DTO Dto { get; set; }
}