package dk.sep3.loadbalancer;

import common.ClientRequest;
import dk.sep3.webapi.WebAPIServer;
import org.springframework.stereotype.Service;

/** ILoadBalancerService implementation **/
@Service
public class LoadBalancerService implements ILoadBalancerService {

    private final WebAPIServerMonitor monitor;

    public LoadBalancerService(WebAPIServerMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public String getAvailableWebApiServer(ClientRequest request) {
        WebAPIServer server = monitor.getAvailableServer();

        if (server == null) {
            return "No servers available";
        }

        return server.getUrl();
    }

    @Override
    public void handleServerFailure(String serverUrl) {
        monitor.notifyServerFailure(serverUrl);
    }

}
