package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;
import org.springframework.context.annotation.Configuration;

/** Handles incoming client requests and forwards them to the appropriate handler **/
@Configuration
public class RequestHandler {

    private final CommunicationClient communicationClient;

    public RequestHandler(CommunicationClient communicationClient) {
        this.communicationClient = communicationClient;
    }

    public ServerResponse handle(ClientRequest request) {
        return communicationClient.callDbServer(request);
    }
}
