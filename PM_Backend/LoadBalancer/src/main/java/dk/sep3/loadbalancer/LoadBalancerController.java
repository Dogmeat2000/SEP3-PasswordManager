package dk.sep3.loadbalancer;

import common.ServerResponse;
import common.dto.DTO;
import common.requests.ClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/** LoadBalancerController returns the address of an available WebAPI server to the client **/
@RestController
@RequestMapping("/loadbalancer")
public class LoadBalancerController {
    private final ILoadBalancerService service;


    public LoadBalancerController(ILoadBalancerService service) {
        this.service = service;
    }

    @PostMapping("/api/assign-server")
    public ResponseEntity<ServerResponse> assignWebApiServer(@RequestBody ClientRequest request) {
        String serverUrl = service.getAvailableWebApiServer(request);
        ServerResponse response = new ServerResponse(serverUrl, 200);

        return ResponseEntity.ok(response);
    }
}
