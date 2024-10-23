package common.requests;

import common.dto.DTO;

public class GetMasterUserClientRequest implements ClientRequest {
    private final String requestType = "GetMasterUser";

    private DTO dto;

    public GetMasterUserClientRequest() {
    }

    public GetMasterUserClientRequest(DTO dto) {
        this.dto = dto;
    }

    @Override
    public DTO getDTO() {
        return null;
    }

    @Override
    public void setDTO(DTO dto) {
        this.dto = dto;
    }
}
