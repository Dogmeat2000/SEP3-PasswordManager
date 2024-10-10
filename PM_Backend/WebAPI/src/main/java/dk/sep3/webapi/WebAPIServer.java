package dk.sep3.webapi;

import dto.ClientRequest;

/** Handles requests from clients and exposes the API-methods to interact with the dbServer **/
public class WebAPIServer {
    private int currentLoad;
    private final int MAX_LOAD = 3;
    private boolean available;

    public WebAPIServer() {
        this.currentLoad = 0;
        this.available = true;
    }

    public void handleRequest(ClientRequest request) {
        currentLoad++;
        System.out.println("Handling request: " + request.ToString());

        // TODO implement

        resetLoad();
    }

    public boolean isActive() {
        return false;
    }

    public boolean isAvailable() {
        return available = currentLoad < MAX_LOAD;
    }

    public void resetLoad() {
        currentLoad -= 1;
        available = true;
    }
}
