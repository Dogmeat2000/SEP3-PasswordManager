package dk.sep3.loadbalancer;

import dk.sep3.webapi.WebAPIServer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Monitor servers to check if they are overloaded or not responding, scaling if necessary through the factory  **/
@Component
public class WebAPIServerMonitor {
    private List<WebAPIServer> servers;
    private final WebAPIServerFactory factory;

    public WebAPIServerMonitor(WebAPIServerFactory factory) {
        this.factory = factory;
        this.servers = new ArrayList<>();

        createNewServer();
    }

    /** Monitor servers every 10 seconds to check if they are overloaded og not responding **/
    @Scheduled(fixedRate = 10000)
    public void monitorServers() {
        List<WebAPIServer> serversToRemove = new ArrayList<>();
        for (WebAPIServer server : servers) {
            if (isServerOverloaded(server)) {
                scaleServers();
            } else if (!server.isActive()) {
                serversToRemove.add(server); // Mark for removal
            }
        }
        serversToRemove.forEach(this::shutdownServer);
        servers.removeAll(serversToRemove);

    }

    /** Assigns the first available server to the client or scales the servers if no servers are available **/
    public WebAPIServer getAvailableServer() {
        return servers.stream().filter(WebAPIServer::isAvailable).findFirst().orElse(createNewServer());
    }

    public WebAPIServer createNewServer() {
        WebAPIServer newServer = factory.createNewServer();
        servers.add(newServer);
        return newServer;
    }

    private void scaleServers() {
        boolean allServersOverloaded = servers.stream().allMatch(this::isServerOverloaded);

        if (allServersOverloaded) {
            createNewServer();
        }
    }

    public void shutdownServer(WebAPIServer server) {
        if (server.getProcess() != null) {
            server.getProcess().destroy();
        }
    }

    private boolean isServerOverloaded(WebAPIServer webAPIServer) {
        return !webAPIServer.isAvailable();
    }

    public void notifyServerFailure(String serverUrl) {
        servers.removeIf(server -> server.getUrl().equals(serverUrl));
    }
}
