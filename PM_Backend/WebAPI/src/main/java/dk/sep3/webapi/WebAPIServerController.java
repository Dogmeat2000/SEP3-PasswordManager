package dk.sep3.webapi;

import common.ServerResponse;
import common.requests.ClientRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WebAPIServerController {
    private final WebAPIServerService service;

    public WebAPIServerController(WebAPIServerService service) {
        this.service = service;
    }

    @PostMapping("/handleRequest")
    public ResponseEntity<ServerResponse> handleRequest(@RequestBody ClientRequest request) {
        // Call the service to handle the request, regardless of the type
        ServerResponse response = service.handleClientRequest(request);
        return ResponseEntity.ok(response);
    }
}
