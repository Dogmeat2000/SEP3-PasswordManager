package dk.sep3.webapi.network.converter;

import common.ServerResponse;
import common.dto.LoginEntryListDTO;
import common.dto.MasterUserDTO;
import grpc.GenericResponse;
import grpc.LoginEntryDTO;
import org.springframework.stereotype.Component;

@Component
public class GrpcToServerResponseConverter {
    public ServerResponse convert(GenericResponse grpcResponse) {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setStatusCode(grpcResponse.getStatusCode());

        // Checks to see if GenericResponse has data of type MasterUserDTO
        if (grpcResponse.hasMasterUser()) {
            MasterUserDTO masterUserDTO = new MasterUserDTO();
            masterUserDTO.setId(grpcResponse.getMasterUser().getId());
            masterUserDTO.setMasterUsername(grpcResponse.getMasterUser().getMasterUsername());
            masterUserDTO.setMasterPassword(grpcResponse.getMasterUser().getMasterPassword());
            serverResponse.setDto(masterUserDTO);

        } else if (grpcResponse.hasLoginEntries()) {
            // Convert gRPC loginEntries to HTTP compatible format:
            LoginEntryListDTO loginEntryListDTO = new LoginEntryListDTO();
            for (LoginEntryDTO grpcLoginEntryDTO : grpcResponse.getLoginEntries().getLoginEntriesList()){
                common.dto.LoginEntryDTO newEntry = new common.dto.LoginEntryDTO(
                    grpcLoginEntryDTO.getEntryUsername(),
                    grpcLoginEntryDTO.getEntryPassword(),
                    grpcLoginEntryDTO.getMasterUserId(),
                    grpcLoginEntryDTO.getEntryName(),
                    grpcLoginEntryDTO.getEntryAddress(),
                    grpcLoginEntryDTO.getCategory()
                );
                loginEntryListDTO.addLoginEntry(newEntry);
            }
            // Set the ServerResponse:
            serverResponse.setDto(loginEntryListDTO);

        } /*else if (grpcResponse.hasLoginEntry()) {
          LoginEntryDTO loginEntryDTO = new LoginEntryDTO(grpcResponse.getLoginEntry().getEntryUsername(),
              grpcResponse.getLoginEntry().getEntryPassword(),
              grpcResponse.getLoginEntry().getMasterUserId(),
              grpcResponse.getLoginEntry().getEntryName(),
              grpcResponse.getLoginEntry().getEntryAddress(),
              "Other");
            serverResponse.setDto(loginEntryDTO);
        }*/ else if (grpcResponse.hasException()) {
            // An exception occurred. Return the exception:
            return new ServerResponse(grpcResponse.getStatusCode(), grpcResponse.getException().getException());

        } else {
            return new ServerResponse(500, "Error: dto not supported");
        }

        return serverResponse;
    }
}
