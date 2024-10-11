package dk.sep3.webapi;

import dto.ClientRequest;
import dto.ServerResponse;

/** Handles requests from clients and exposes the API-methods to interact with the dbServer **/
public class WebAPIServer {
    private int currentLoad;
    private final int MAX_LOAD = 3;
    private boolean available;

    public WebAPIServer() {
        this.currentLoad = 0;
        this.available = true;
    }

    public ServerResponse handleRequest(ClientRequest request) {
        if (!isAvailable()) {
            return new ServerResponse("Server is overloaded, cannot handle more requests.", 503);
        }

        // TODO: Implement logic to handle the request

        ServerResponse response = new ServerResponse("Request handled successfully", 200);
        finishRequest();

        return response;
    }

    private void finishRequest() {
        System.out.println("Finished request. Reducing load.");
        resetLoad();
    }

    public boolean isActive() {
        return true;
    }

    public boolean isAvailable() {
        return available = currentLoad < MAX_LOAD;
    }

    public void resetLoad() {
        if (currentLoad > 0) {
            currentLoad--;
        }
        available = true;
    }
}
