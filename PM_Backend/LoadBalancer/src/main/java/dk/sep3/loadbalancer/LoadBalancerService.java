package dk.sep3.loadbalancer;

import dk.sep3.webapi.WebAPIServiceMonitor;
import dk.sep3.webapi.WebAPIServer;
import dto.ClientRequest;
import dto.ServerResponse;
import org.springframework.stereotype.Service;

/** Receives a request from LoadBalancerController and forwards it to the available WebAPIServer
 * Handles all business logic for the LoadBalancer **/
@Service
public class LoadBalancerService implements ILoadBalancerService {
    private final WebAPIServiceMonitor monitor;

    public LoadBalancerService(WebAPIServiceMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public ServerResponse handleClientRequest(ClientRequest request) {
        WebAPIServer availableServer = monitor.assignAvailableServer();
        return availableServer.handleRequest(request);
    }
}
