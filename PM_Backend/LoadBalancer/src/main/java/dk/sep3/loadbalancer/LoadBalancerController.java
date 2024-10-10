package dk.sep3.loadbalancer;

import dto.ClientRequest;
import dto.ServerResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** LoadBalancerController receives a ClientRequest and forwards it to LoadBalancerService.
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
            service.handleClientRequest(request);
            return new ServerResponse("Request handled successfully", 200);
        } catch (RuntimeException e) {
            return new ServerResponse(e.getMessage(), 503);
        }
    }
}
