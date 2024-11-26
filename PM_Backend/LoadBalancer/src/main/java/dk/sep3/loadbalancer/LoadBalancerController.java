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
