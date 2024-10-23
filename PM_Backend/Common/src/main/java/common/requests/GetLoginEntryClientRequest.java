package common.requests;

import common.dto.DTO;

public class GetLoginEntryClientRequest implements ClientRequest {
    private DTO dto;
    private final String requestType = "GetLoginEntry";

    public GetLoginEntryClientRequest() {
    }

    public GetLoginEntryClientRequest(DTO dto) {
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
