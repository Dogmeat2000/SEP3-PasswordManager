package common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import common.dto.DTO;

public class ClientRequest {

    @JsonProperty("requestType")
    private String requestType;

    @JsonProperty("dto")
    private DTO dto;

    @JsonCreator
    public ClientRequest() {}

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
