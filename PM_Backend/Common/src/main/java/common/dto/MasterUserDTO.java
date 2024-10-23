package common.dto;

public class MasterUserDTO extends DTO {
    private String masterUsername;
    private String masterPassword;


    public MasterUserDTO(String masterUsername, String masterPassword) {
        this.masterUsername = masterUsername;
        this.masterPassword = masterPassword;
    }

    public String getMasterUsername() {
        return masterUsername;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }
}
