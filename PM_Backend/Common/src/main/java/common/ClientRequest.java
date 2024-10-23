package common;

import common.dto.DTO;

public class ClientRequest {

    private String requestType;
    private DTO dto;

    public ClientRequest(String requestType, DTO dto) {
        this.requestType = requestType;
        this.dto = dto;
    }

    public String getRequestType() {
        return requestType;
    }

    public DTO getDTO() {
        return dto;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setDTO(DTO dto) {
        this.dto = dto;
    }
}
