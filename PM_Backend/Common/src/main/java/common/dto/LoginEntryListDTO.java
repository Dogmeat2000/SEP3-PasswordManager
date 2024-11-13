package common.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public class LoginEntryListDTO extends DTO {
    public List<LoginEntryDTO> loginEntries;

    public LoginEntryListDTO(int id, List<LoginEntryDTO> loginEntries) {
        super.setId(id);
        this.loginEntries = loginEntries;
    }

    public LoginEntryListDTO() {
        loginEntries = new ArrayList<>();
    }

    public List<LoginEntryDTO> getLoginEntries() {
        return loginEntries;
    }

    public void setLoginEntries(List<LoginEntryDTO> loginEntries) {
        this.loginEntries = loginEntries;
    }

    public void addLoginEntry(LoginEntryDTO loginEntry) {
        loginEntries.add(loginEntry);
    }
}
