package common.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)

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
