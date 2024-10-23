package common.requests;

import common.dto.DTO;

public interface ClientRequest {
    String getRequestType();
    void setRequestType(String requestType);
    DTO getDTO();
    void setDTO(DTO dto);
}
