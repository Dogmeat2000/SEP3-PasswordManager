package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;
import common.dto.LoginEntryDTO;
import org.springframework.context.annotation.Configuration;

/** Handles incoming client requests from WebAPIServer and forwards them to the CommunicationClient **/
@Configuration
public class RequestHandler {

    private final CommunicationClient communicationClient;
    public RequestHandler(CommunicationClient communicationClient) {
        this.communicationClient = communicationClient;
    }

    public ServerResponse handle(ClientRequest request) {
        if (request.getDTO() instanceof common.dto.LoginEntryDTO) {
            System.out.println("WebAPI: " + ((LoginEntryDTO) request.getDTO()).getEntryName());
        }
        System.out.println("WebAPI: requestType " + request.getRequestType() + " dto: " + request.getDTO().getId());

        ServerResponse response = communicationClient.callDbServer(request);
        System.out.println("WebAPI: Response: " + response.getStatusCode());

        return response;
    }
}
