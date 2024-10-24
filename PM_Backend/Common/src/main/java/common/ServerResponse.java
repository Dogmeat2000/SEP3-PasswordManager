package common;

import common.dto.DTO;
import common.dto.MasterUserDTO;

public class ServerResponse {
    private String message;
    private int statusCode;
    private DTO dto;

    // Tom konstruktør kræves for deserialisering
    public ServerResponse() {}

    public ServerResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ServerResponse(int statusCode, DTO dto) {
        this.statusCode = statusCode;
        this.dto = dto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public DTO getDto() {
        return dto;
    }

    public void setDto(DTO dto) {
        this.dto = dto;
    }

}
