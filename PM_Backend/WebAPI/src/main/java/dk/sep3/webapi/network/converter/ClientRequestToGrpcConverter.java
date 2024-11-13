package dk.sep3.webapi.network.converter;

import common.ClientRequest;
import common.dto.LoginEntryDTO;
import grpc.GenericRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientRequestToGrpcConverter {

    public GenericRequest convert(ClientRequest request) {
        GenericRequest.Builder builder = GenericRequest.newBuilder()
                .setRequestType(request.getRequestType());

        // Tilføj DTO baseret på requestType
        if (request.getRequestType().equals("CreateMasterUser")) {
            common.dto.MasterUserDTO masterUserDTO = (common.dto.MasterUserDTO) request.getDTO();
            builder.setMasterUser(
                    grpc.MasterUserDTO.newBuilder()
                            .setId(masterUserDTO.getId())
                            .setMasterUsername(masterUserDTO.getMasterUsername())
                            .setMasterPassword(masterUserDTO.getMasterPassword())
                            .build()
            );
        } else if (request.getRequestType().equals("ReadMasterUser")) {
                common.dto.MasterUserDTO masterUserDTO = (common.dto.MasterUserDTO) request.getDTO();

                builder.setMasterUser(
                        grpc.MasterUserDTO.newBuilder()
                                .setMasterUsername(masterUserDTO.getMasterUsername())
                                .setMasterPassword(masterUserDTO.getMasterPassword())
                                .build()
                );
        } else if (request.getRequestType().equals("CreateLoginEntry"))
        {
            common.dto.LoginEntryDTO loginEntryDTO = (LoginEntryDTO) request.getDTO();
            builder.setLoginEntry(
                    grpc.LoginEntryDTO.newBuilder()
                            .setId(loginEntryDTO.getId())
                            .setEntryUsername(loginEntryDTO.getEntryUsername())
                            .setEntryPassword(loginEntryDTO.getEntryPassword())
                            .setMasterUserId(loginEntryDTO.getMasterUserId())
                            .build()
            );
        }


        return builder.build();
    }
}
