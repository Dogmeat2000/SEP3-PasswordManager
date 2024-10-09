package dk.sep3.passwordmanager.loadBalancer.service;

import dk.sep3.passwordmanager.loadBalancer.client.IClientRequestHandler;
import dk.sep3.passwordmanager.loadBalancer.dto.ClientRequest;
import dk.sep3.passwordmanager.loadBalancer.server.IDbServerCommunicator;

// Uses IClientRequestHandler and IDbServerCommunicator to control and handling client requests
public class LoadBalancerService implements ILoadBalancerService {
    private final IClientRequestHandler clientRequestHandler;
    private final IDbServerCommunicator dbServerCommunicator;
    private int currentLoad = 0; // Current load of users on the loadBalancer

    public LoadBalancerService(IClientRequestHandler clientRequestHandler, IDbServerCommunicator dbServerCommunicator) {
        this.clientRequestHandler = clientRequestHandler;
        this.dbServerCommunicator = dbServerCommunicator;
    }


    @Override
    public void handleLoad(ClientRequest request) {
        // TODO implement load balancing, remember to increment and decrement currentLoad
    }

    @Override
    public int getCurrentLoad() {
        return currentLoad;
    }
}
