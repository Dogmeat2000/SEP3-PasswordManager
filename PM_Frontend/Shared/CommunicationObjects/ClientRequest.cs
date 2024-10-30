using Shared.Dtos;

namespace Shared.CommunicationObjects;

public class ClientRequest
{
    public ClientRequest()
    {
    }

    public ClientRequest(string requestType, DTO dto)
    {
        RequestType = requestType;
        Dto = dto;
    }

    public string RequestType { get; set; }
    public DTO Dto { get; set; }
}