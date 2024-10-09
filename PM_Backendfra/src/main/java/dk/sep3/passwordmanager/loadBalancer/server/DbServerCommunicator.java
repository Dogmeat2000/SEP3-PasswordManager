package dk.sep3.passwordmanager.loadBalancer.server;

import dk.sep3.passwordmanager.loadBalancer.dto.ClientRequest;
import dk.sep3.passwordmanager.loadBalancer.dto.ServerResponse;

// Implementation for handling requests to database-server
public class DbServerCommunicator implements IDbServerCommunicator{
    @Override
    public ServerResponse sendToDatabase(ClientRequest request) {
        // TODO implement send to DB
        return null;
    }
}
