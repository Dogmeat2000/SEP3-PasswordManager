namespace Shared.Dtos;

public abstract class DTO {
    
    private int id { get; set; }

    public DTO(int id)
    {
        this.id = id;
    }

    
}