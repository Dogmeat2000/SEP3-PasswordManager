package dk.sep3.passwordmanager.loadBalancer.factory;

import dk.sep3.passwordmanager.loadBalancer.client.IClientRequestHandler;
import dk.sep3.passwordmanager.loadBalancer.server.DbServerCommunicator;
import dk.sep3.passwordmanager.loadBalancer.server.IDbServerCommunicator;
import dk.sep3.passwordmanager.loadBalancer.service.LoadBalancerService;

// Factory for creating LoadBalancerService
public class LoadBalancerFactory {
    public LoadBalancerService createLoadBalancer() {
        IClientRequestHandler clientRequestHandler = new ClientRequestHandler();
        IDbServerCommunicator dbServerCommunicator = new DbServerCommunicator();

        return new LoadBalancerService(clientRequestHandler,dbServerCommunicator);
    }
}
