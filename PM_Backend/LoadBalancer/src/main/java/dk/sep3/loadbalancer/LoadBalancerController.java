package dk.sep3.loadbalancer;

import dto.ClientRequest;
import dto.ServerResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** LoadBalancerController receives a dto.ClientRequest and forwards it to LoadBalancerService.
 * Recieves and handles all HTTP requests from clients **/
@RestController
@RequestMapping("/loadbalancer")
public class LoadBalancerController {
    private final ILoadBalancerService service;

    public LoadBalancerController(ILoadBalancerService service) {
        this.service = service;
    }

    @PostMapping("/request")
    public ServerResponse handleRequest(@RequestBody ClientRequest request) {
        try {
            return service.handleClientRequest(request);
        } catch (RuntimeException e) {
            return new ServerResponse("An error occurred", 500);
        }
    }
}
