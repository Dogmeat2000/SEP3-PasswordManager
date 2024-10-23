package dk.sep3.webapi;

import common.ClientRequest;
import common.ServerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WebAPIServerController {
    private final WebAPIServer server;

    public WebAPIServerController(WebAPIServer server) {
        this.server = server;
    }

    @PostMapping("/handleRequest")
    public ResponseEntity<ServerResponse> handleRequest(@RequestBody ClientRequest request) {
        ServerResponse response = server.handleRequest(request);
        return ResponseEntity.ok(response);
    }
}
