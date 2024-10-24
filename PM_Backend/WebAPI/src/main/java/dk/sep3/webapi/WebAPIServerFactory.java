package dk.sep3.webapi;

import dk.sep3.webapi.network.RequestHandler;
import org.springframework.stereotype.Component;

/** Factory for creating new WebAPIServer instances **/
@Component
public class WebAPIServerFactory {

    private final RequestHandler requestHandler;

    public WebAPIServerFactory(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public WebAPIServer createNewServer() {
        // String newServerUrl = "http://localhost:" + getNextAvailablePort();
        String newServerUrl = "http://localhost:8081";

        WebAPIServer newServer = new WebAPIServer(requestHandler);
        newServer.setUrl(newServerUrl);

        System.out.println("Created new server: " + newServerUrl);
        return newServer;
    }

    private int getNextAvailablePort() {
        return 8081 + (int) (Math.random() * 10);
    }

}
