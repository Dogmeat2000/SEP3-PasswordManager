using Shared.Dtos;

namespace Shared.CommunicationObjects;

public class ClientRequest
{
    public ClientRequest()
    {
    }

    public ClientRequest(string requestType, DTO dto)
    {
        this.requestType = requestType;
        this.dto = dto;
    }

    public string requestType { get; set; }
    public DTO dto { get; set; }
}