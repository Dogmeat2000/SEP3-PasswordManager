package common.requests;

import common.dto.DTO;

public class CreateMasterUserClientRequest implements ClientRequest {
    private final String requestType = "CreateMasterUser";

    private DTO dto;

    public CreateMasterUserClientRequest() {
    }

    public CreateMasterUserClientRequest(DTO dto) {
        this.dto = dto;
    }

    @Override
    public DTO getDTO() {
        return null;
    }

    public void setDTO(DTO dto) {
        this.dto = dto;
    }
}
