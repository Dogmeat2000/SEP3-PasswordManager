package dk.sep3.loadbalancer;

import dk.sep3.webapi.WebAPIServer;
import dk.sep3.webapi.WebAPIServerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Monitor servers to check if they are overloaded or not responding, scaling if necessary through the factory  **/
@Component
public class WebAPIServiceMonitor {
    private final List<WebAPIServer> WEB_API_SERVERS;
    private final WebAPIServerFactory factory;

    public WebAPIServiceMonitor(WebAPIServerFactory factory) {
        this.factory = factory;
        this.WEB_API_SERVERS = new ArrayList<>();
    }

    /** Monitor servers every 10 seconds to check if they are overloaded og not responding **/
    @Scheduled(fixedRate = 10000)
    public void monitorServers() {
        for (WebAPIServer server : WEB_API_SERVERS) {
            if (isServerOverloaded(server)) {
                scaleServers();
            } else if (!server.isActive()) {
                WEB_API_SERVERS.remove(server);
            }
        }
    }

    /** Assigns the first available server to the client or scales the servers if no servers are available **/
    public WebAPIServer assignAvailableServer() {
        return WEB_API_SERVERS.stream().filter(WebAPIServer::isAvailable).findFirst().orElse(createNewServer());
    }

    public WebAPIServer createNewServer() {
        WebAPIServer newServer = factory.createNewServer();
        WEB_API_SERVERS.add(newServer);

        return newServer;
    }

    private void scaleServers() {
        boolean allServersOverloaded = WEB_API_SERVERS.stream().allMatch(this::isServerOverloaded);

        if (allServersOverloaded) {
            createNewServer();
        }
    }

    private boolean isServerOverloaded(WebAPIServer server) {
        return false;
    }


}
