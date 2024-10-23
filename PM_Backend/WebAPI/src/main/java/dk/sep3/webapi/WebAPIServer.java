package dk.sep3.webapi;

import common.requests.ClientRequest;
import common.ServerResponse;
import dk.sep3.webapi.network.RequestHandler;
import io.grpc.StatusRuntimeException;

/** Handles incoming client requests and forwards them to the appropriate handler **/
public class WebAPIServer {
    private int currentLoad;
    private final int MAX_LOAD = 3;
    private boolean available;
    private String url;
    private RequestHandler handler;


    public WebAPIServer(String url, RequestHandler handler) {
        this.url = url;
        this.currentLoad = 0;
        this.available = true;
        this.handler = handler;
    }

    public ServerResponse handleRequest(ClientRequest request) {
        if (!isAvailable()) {
            return new ServerResponse("Server is overloaded. Please try again later.", 503);
        }

        try {
            currentLoad++;

            ServerResponse response = handler.handle(request);
            finishRequest();
            return response;
        } catch (StatusRuntimeException e) {
            return new ServerResponse("Error: " + e.getStatus().getDescription(), 500);
        }
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

    public String getUrl() {
        return url;
    }

}
