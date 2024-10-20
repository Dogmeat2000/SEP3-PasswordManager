package dto;

public class ClientRequest {
    private String requestType;
    private String requestData;

    // Empty constructor required for deserialization
    public ClientRequest() {}

    public ClientRequest(String requestType, String requestData) {
        this.requestType = requestType;
        this.requestData = requestData;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }
}
