package dk.sep3.passwordmanager.loadBalancer.client;

import dk.sep3.passwordmanager.loadBalancer.dto.ClientRequest;

// Interface for handling client requests
public interface IClientRequestHandler {
    public void handleRequest(ClientRequest request);
}
