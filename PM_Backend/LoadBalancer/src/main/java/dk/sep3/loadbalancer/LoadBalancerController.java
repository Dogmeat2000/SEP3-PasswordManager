package dk.sep3.loadbalancer;

import common.ClientRequest;
import common.ServerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** LoadBalancerController returns the address of an available WebAPI server to the client **/
@RestController
@RequestMapping("/loadbalancer")
public class LoadBalancerController {
    private final ILoadBalancerService service;

    public LoadBalancerController(ILoadBalancerService service) {
        this.service = service;
    }

    /**
     * Assigns an available WebAPI server to the client.
     *
     * @param request The client request containing necessary information to determine an available server.
     * @return ResponseEntity containing the server response with the URL of the assigned WebAPI server.
     */
    @PostMapping("/server")
    public ResponseEntity<ServerResponse> assignWebApiServer(@RequestBody ClientRequest request) {
        String serverUrl = service.getAvailableWebApiServer(request);
        System.out.println("Server URL: " + serverUrl);
        ServerResponse response = new ServerResponse(200, serverUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public String home() {
        return "LoadBalancer is running!";
    }

}
