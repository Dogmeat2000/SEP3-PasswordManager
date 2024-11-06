package dk.sep3.webapi;

import common.ClientRequest;
import common.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller for handling incoming requests from the client, and forwarding them to the WebAPIServer **/
@RestController
@RequestMapping("/api")
public class WebAPIServerController {
    private final WebAPIServer server;

    public WebAPIServerController(WebAPIServer server) {
        this.server = server;
    }

    @PostMapping("/handleRequest")
    public ResponseEntity<ServerResponse> handleRequest(@RequestBody ClientRequest request) {
        System.out.println("Received request: " + request + " | Request Type:" + request.getRequestType());
        ServerResponse response = server.handleRequest(request);

        HttpStatus status = HttpStatus.resolve(response.getStatusCode());

        if(status != null)
            return new ResponseEntity<>(response, status);
        else
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
