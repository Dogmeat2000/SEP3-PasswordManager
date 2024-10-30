using Shared.Dtos;

namespace Shared.CommunicationObjects;

public class ClientRequest
{
    public string RequestType { get; set; }
    public DTO Dto { get; set; } 

    public ClientRequest() {}

    public ClientRequest(string requestType, DTO dto)
    {
        RequestType = requestType;
        Dto = dto;
    }
}