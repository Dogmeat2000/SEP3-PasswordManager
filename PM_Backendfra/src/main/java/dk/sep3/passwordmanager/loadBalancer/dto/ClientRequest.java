package dk.sep3.passwordmanager.loadBalancer.dto;

public class ClientRequest {
    private String requestType;
    private String requestData;
    private String clientId;

    public ClientRequest(String requestType, String requestData, String clientId) {
        this.requestType = requestType;
        this.requestData = requestData;
        this.clientId = clientId;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getRequestData() {
        return requestData;
    }

    public String getClientId() {
        return clientId;
    }
}
