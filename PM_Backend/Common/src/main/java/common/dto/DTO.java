package common.dto;

public abstract class DTO {
    private int id;

    public DTO() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
