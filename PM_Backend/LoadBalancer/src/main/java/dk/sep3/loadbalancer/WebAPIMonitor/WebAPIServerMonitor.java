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

    /**
     * Monitor servers every 10 seconds to check if they are overloaded or not responding.
     * Scales servers if necessary.
     */
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

    /**
     * Assigns the first available server to the client or scales the servers if no servers are available.
     *
     * @return An available WebAPIServer instance, or a new one if none are available.
     */
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

    /**
     * Creates and starts a new WebAPIServer instance.
     *
     * @return The newly created WebAPIServer instance.
     */
    public WebAPIServer createNewServer() {
        WebAPIServer newServer = factory.createNewServer();
        servers.add(newServer);
        return newServer;
    }

    /**
     * Scales up the servers by creating a new one if all current servers are overloaded.
     */
    private void scaleServers() {
        boolean allServersOverloaded = servers.stream().allMatch(this::isServerOverloaded);

        if (allServersOverloaded) {
            createNewServer();
        }
    }

    /**
     * Shuts down the specified server and terminates its process if running.
     *
     * @param server The WebAPIServer instance to shut down.
     */
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

    /**
     * Checks if the given server is overloaded.
     *
     * @param webAPIServer The WebAPIServer instance to check.
     * @return True if the server is overloaded, false otherwise.
     */
    private boolean isServerOverloaded(WebAPIServer webAPIServer) {
        return !webAPIServer.isAvailable();
    }

    /**
     * Notifies the monitor of a server failure and removes the server from the list of managed servers.
     *
     * @param serverUrl The URL of the server that has failed.
     */
    public void notifyServerFailure(String serverUrl) {
        servers.removeIf(server -> server.getUrl().equals(serverUrl));
    }

    /**
     * Shuts down all running servers when the application is terminated.
     */
    @Override
    public void destroy() {
        System.out.println("Shutting down all WebAPI servers...");
        servers.forEach(this::shutdownServer);
    }
}
