package dk.sep3.webapi;

import org.springframework.stereotype.Component;

@Component
public class WebAPIServerFactory {
    public WebAPIServer createNewServer() {
        return new WebAPIServer();
    }
}
