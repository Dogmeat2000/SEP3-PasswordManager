package dk.sep3.loadbalancer;

import dk.sep3.webapi.WebAPIServer;
import dto.ClientRequest;
import org.springframework.stereotype.Service;

@Service
public class LoadBalancerService implements ILoadBalancerService {
    private final WebAPIServiceMonitor monitor;

    /** Receives a request from LoadBalancerController and forwards it to the available WebAPIServer
     * Handles all business logic for the LoadBalancer **/

    public LoadBalancerService(WebAPIServiceMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void handleClientRequest(ClientRequest request) {
        WebAPIServer availableServer = monitor.assignAvailableServer();
        availableServer.handleRequest(request);
    }
}
