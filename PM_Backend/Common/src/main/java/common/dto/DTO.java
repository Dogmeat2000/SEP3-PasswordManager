package common.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MasterUserDTO.class, name = "MasterUserDTO"),
        @JsonSubTypes.Type(value = LoginEntryDTO.class, name = "LoginEntryDTO"),
        @JsonSubTypes.Type(value = LoginEntryListDTO.class, name = "LoginEntryListDTO")
})

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
