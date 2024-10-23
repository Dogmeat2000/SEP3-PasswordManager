package dk.sep3.webapi.network;

import common.ServerResponse;
import common.requests.ClientRequest;
import common.requests.GetLoginEntryClientRequest;
import common.requests.GetMasterUserClientRequest;
import dk.sep3.webapi.network.CommunicationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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
