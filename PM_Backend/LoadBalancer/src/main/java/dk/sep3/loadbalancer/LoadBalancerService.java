package dk.sep3.loadbalancer;

import common.requests.ClientRequest;
import dk.sep3.webapi.WebAPIServer;
import dk.sep3.webapi.WebAPIServerMonitor;
import org.springframework.beans.factory.annotation.Autowired;
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
