package dk.sep3.webapi;

import dk.sep3.webapi.network.RequestHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Factory for creating new WebAPIServer instances **/
@Component
public class WebAPIServerFactory {

    private final RequestHandler requestHandler;

    public WebAPIServerFactory(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public WebAPIServer createNewServer() {
        String newServerUrl = "http://localhost:" + getNextAvailablePort();
        return new WebAPIServer(newServerUrl, requestHandler);
    }

    private int getNextAvailablePort() {
        return 8083 + (int) (Math.random() * 10);
    }

}
