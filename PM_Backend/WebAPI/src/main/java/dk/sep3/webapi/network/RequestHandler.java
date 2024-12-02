package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;
import org.springframework.context.annotation.Configuration;

/** Handles incoming client requests from WebAPIServer and forwards them to the CommunicationClient **/
@Configuration
public class RequestHandler {

    private final CommunicationClient communicationClient;
    public RequestHandler(CommunicationClient communicationClient) {
        this.communicationClient = communicationClient;
    }

    /**
     * Handles an incoming client request by forwarding it to the CommunicationClient for processing.
     *
     * @param request The client request to be handled.
     * @return A ServerResponse indicating the result of the request.
     */
    public ServerResponse handle(ClientRequest request) {
        return communicationClient.callDbServer(request);
    }
}
