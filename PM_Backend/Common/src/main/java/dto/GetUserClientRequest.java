package dto;

public class GetUserClientRequest extends ClientRequest {
    private String username;
    private String password;

    public GetUserClientRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
