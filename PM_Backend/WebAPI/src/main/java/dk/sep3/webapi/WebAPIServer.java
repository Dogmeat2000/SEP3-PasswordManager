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
    private String url;
    private RequestHandler handler;
    private Process process;

    public WebAPIServer(RequestHandler handler) {
        this.currentLoad = 0;
        this.handler = handler;
    }

    /**
     * Handles an incoming client request by forwarding it to the request handler.
     *
     * @param request The client request to be handled.
     * @return A ServerResponse indicating the result of the request.
     */
    public ServerResponse handleRequest(ClientRequest request) {
        if (!isAvailable()) {
            return new ServerResponse(503, "Server is overloaded. Please try again later.");
        }

        try {
            currentLoad++;
            System.out.println("Handling request. Current load: " + currentLoad);

            ServerResponse response = handler.handle(request);
            finishRequest();  // Ensure load is reduced after handling the request
            return response;
        } catch (StatusRuntimeException e) {
            finishRequest();  // Reduce load even if an error occurs
            return new ServerResponse(500, "Error cannot reach dbServer: " + e.getStatus().getDescription());
        }
    }

    /**
     * Reduces the current load by decrementing the load count.
     */
    private void finishRequest() {
        currentLoad--;
        System.out.println("Finished request. Current load: " + currentLoad);
    }

    public boolean isActive() {
        return process != null && process.isAlive();
    }

    public boolean isAvailable() {
        return currentLoad < MAX_LOAD;
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
