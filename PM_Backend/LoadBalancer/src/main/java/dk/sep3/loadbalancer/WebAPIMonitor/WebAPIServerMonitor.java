package dk.sep3.loadbalancer.WebAPIMonitor;

import dk.sep3.webapi.WebAPIServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Monitor servers to check if they are overloaded or not responding, scaling if necessary through the factory  **/
@Component
public class WebAPIServerMonitor implements DisposableBean {
    private final List<WebAPIServer> servers;
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
                serversToRemove.add(server);
            }
        }
        serversToRemove.forEach(this::shutdownServer);
        servers.removeAll(serversToRemove);
    }

    /** Assigns the first available server to the client or scales the servers if no servers are available **/
    public WebAPIServer getAvailableServer() {
        WebAPIServer availableServer = servers.stream().filter(WebAPIServer::isAvailable).findFirst().orElse(null);
        if (availableServer == null) {
            System.out.println("No available servers, creating a new one.");
            return createNewServer();
        } else {
            System.out.println("Found available server: " + availableServer.getUrl());
            return availableServer;
        }
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
            try {
                server.getProcess().waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean isServerOverloaded(WebAPIServer webAPIServer) {
        return !webAPIServer.isAvailable();
    }

    public void notifyServerFailure(String serverUrl) {
        servers.removeIf(server -> server.getUrl().equals(serverUrl));
    }

    @Override
    public void destroy() {
        System.out.println("Shutting down all WebAPI servers...");
        servers.forEach(this::shutdownServer);
    }
}
