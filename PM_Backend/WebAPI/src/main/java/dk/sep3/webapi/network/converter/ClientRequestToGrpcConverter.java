package dk.sep3.webapi.network.converter;

import common.ClientRequest;
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
        }

        return builder.build();
    }
}
