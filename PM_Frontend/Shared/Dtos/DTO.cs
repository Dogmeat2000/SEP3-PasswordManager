namespace Shared.Dtos;

public class DTO
{
    public DTO(int? id)
    {
        this.id = id;
    }

    public DTO()
    {
    }

    private int? id { get; set; }
}