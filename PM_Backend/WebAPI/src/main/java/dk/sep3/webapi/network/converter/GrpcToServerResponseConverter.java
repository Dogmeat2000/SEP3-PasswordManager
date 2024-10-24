package dk.sep3.webapi.network.converter;

import common.ServerResponse;
import common.dto.LoginEntryDTO;
import common.dto.MasterUserDTO;
import grpc.GenericResponse;
import org.springframework.stereotype.Component;

@Component
public class GrpcToServerResponseConverter {
    public ServerResponse convert(GenericResponse grpcResponse) {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setStatusCode(grpcResponse.getStatusCode());

        // Checks to see if GenericResponse has data of type MasterUserDTO
        if (grpcResponse.hasMasterUser()) {
            MasterUserDTO masterUserDTO = new MasterUserDTO();
            masterUserDTO.setId((int) grpcResponse.getMasterUser().getId());
            masterUserDTO.setMasterUsername(grpcResponse.getMasterUser().getMasterUsername());
            masterUserDTO.setMasterPassword(grpcResponse.getMasterUser().getMasterPassword());
            serverResponse.setDto(masterUserDTO);

        } /* else if (grpcResponse.hasLoginEntry()) {
            LoginEntryDTO loginEntryDTO = new LoginEntryDTO(
                    (int) grpcResponse.getLoginEntry().getId(),
                    grpcResponse.getLoginEntry().getEntryUsername(),
                    grpcResponse.getLoginEntry().getEntryPassword(),
                    (int) grpcResponse.getLoginEntry().getMasterUserId()
            );
            serverResponse.setDto(loginEntryDTO);

        } */ else {
            throw new UnsupportedOperationException("Unknown DTO type in GenericResponse.");
        }

        return serverResponse;
    }
}
