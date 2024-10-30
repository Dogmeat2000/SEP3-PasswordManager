package dk.sep3.webapi;

import common.ClientRequest;
import common.ServerResponse;
import dk.sep3.webapi.network.RequestHandler;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

/** Handles incoming client requests and forwards them to the Request handler **/
@Component
public class WebAPIServer {
    private int currentLoad;
    private final int MAX_LOAD = 3;
    private boolean available;
    private String url;
    private RequestHandler handler;
    private Process process;


    public WebAPIServer(RequestHandler handler) {
        this.currentLoad = 0;
        this.available = true;
        this.handler = handler;
    }

    public ServerResponse handleRequest(ClientRequest request) {
        if (!isAvailable()) {
            return new ServerResponse(503, "Server is overloaded. Please try again later.");
        }

        try {
            currentLoad++;

            ServerResponse response = handler.handle(request);
            finishRequest();
            return response;
        } catch (StatusRuntimeException e) {
            return new ServerResponse(500,"Error: " + e.getStatus().getDescription());
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

    public void setUrl(String url) {
        this.url = url;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
