package dk.sep3.webapi.network.converter;

import common.ClientRequest;
import common.dto.LoginEntryDTO;
import common.dto.MasterUserDTO;
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
        } else if (request.getRequestType().equals("CreateLoginEntry")) {
            common.dto.LoginEntryDTO loginEntryDTO = (LoginEntryDTO) request.getDTO();
            builder.setLoginEntry(
                    grpc.LoginEntryDTO.newBuilder()
                            .setId(loginEntryDTO.getId())
                            .setEntryUsername(loginEntryDTO.getEntryUsername())
                            .setEntryPassword(loginEntryDTO.getEntryPassword())
                            .setMasterUserId(loginEntryDTO.getMasterUserId())
                            .setEntryName(loginEntryDTO.getEntryName())
                            .setEntryAddress(loginEntryDTO.getEntryAddress())
                            .setCategory(loginEntryDTO.getEntryCategory())
                            .build()
            );
        } else if (request.getRequestType().equalsIgnoreCase("ReadLoginEntries")) {
            // Extract MasterUser from request:
            common.dto.MasterUserDTO masterUserDTO = (MasterUserDTO) request.getDTO();

            // Build gRPC compatible request:
            builder.setMasterUser(
                grpc.MasterUserDTO.newBuilder()
                    .setId(masterUserDTO.getId())
                    .setMasterUsername(masterUserDTO.getMasterUsername())
                    .setMasterPassword(masterUserDTO.getMasterPassword())
                    .build()
            );
        }


        return builder.build();
    }
}
