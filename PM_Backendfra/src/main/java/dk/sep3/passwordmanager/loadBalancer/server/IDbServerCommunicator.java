package dk.sep3.passwordmanager.loadBalancer.server;

import dk.sep3.passwordmanager.loadBalancer.dto.ClientRequest;
import dk.sep3.passwordmanager.loadBalancer.dto.ServerResponse;

// Interface for handling requests to database-server
public interface IDbServerCommunicator {

    ServerResponse sendToDatabase(ClientRequest request);
}
