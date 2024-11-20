package common.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public class LoginEntryDTO extends DTO {
    private int id;
    private String entryUsername;
    private String entryPassword;
    private int masterUserId;
    private String entryName;
    private String entryAddress;
    private String entryCategory;


    public LoginEntryDTO(String entryUsername, String entryPassword, int masterUserId, String entryName, String entryAddress, String entryCategory) {
        this.entryUsername = entryUsername;
        this.entryPassword = entryPassword;
        this.masterUserId = masterUserId;
        this.entryName = entryName;
        this.entryAddress = entryAddress;
        this.entryCategory = entryCategory;
    }

    public LoginEntryDTO() {
    }

    public String getEntryUsername() {
        return entryUsername;
    }

    public void setEntryUsername(String entryUsername) {
        this.entryUsername = entryUsername;
    }

    public String getEntryPassword() {
        return entryPassword;
    }

    public void setEntryPassword(String entryPassword) {
        this.entryPassword = entryPassword;
    }

    public int getMasterUserId() {
        return masterUserId;
    }

    public void setMasterUserId(int masterUserId) {
        this.masterUserId = masterUserId;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getEntryAddress() {
        return entryAddress;
    }

    public void setEntryAddress(String entryAddress) {
        this.entryAddress = entryAddress;
    }

    public String getEntryCategory() {
        return entryCategory;
    }

    public void setEntryCategory(String entryCategory) {
        this.entryCategory = entryCategory;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        LoginEntryDTO that = (LoginEntryDTO) o;
        return getId() == that.getId() && getMasterUserId() == that.getMasterUserId() && Objects.equals(getEntryUsername(), that.getEntryUsername()) && Objects.equals(getEntryPassword(),
            that.getEntryPassword()) && Objects.equals(getEntryName(), that.getEntryName()) && Objects.equals(getEntryAddress(), that.getEntryAddress()) && Objects.equals(getEntryCategory(),
            that.getEntryCategory());
    }

    @Override public int hashCode() {
        return Objects.hash(getId(), getEntryUsername(), getEntryPassword(), getMasterUserId(), getEntryName(), getEntryAddress(), getEntryCategory());
    }

    @Override public String toString() {
        return "LoginEntryDTO{" + "id=" + id + ", entryUsername='" + entryUsername + '\'' + ", entryPassword='" + entryPassword + '\'' + ", masterUserId=" + masterUserId + ", entryName='" + entryName
            + '\'' + ", entryAddress='" + entryAddress + '\'' + ", entryCategory='" + entryCategory + '\'' + '}';
    }
}
