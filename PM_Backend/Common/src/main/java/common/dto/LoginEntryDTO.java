package common.dto;

public class LoginEntryDTO extends DTO {
    private String entryUsername;
    private String entryPassword;
    private int masterUserId;

    public LoginEntryDTO(int id, int masterUserId) {
        super.setId(id);
        this.masterUserId = masterUserId;
    }

    public LoginEntryDTO(int id, String entryUsername, String entryPassword, int masterUserId) {
        super.setId(id);
        this.entryUsername = entryUsername;
        this.entryPassword = entryPassword;
        this.masterUserId = masterUserId;
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
}
