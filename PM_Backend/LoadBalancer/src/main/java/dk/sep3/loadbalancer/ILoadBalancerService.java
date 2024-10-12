package dk.sep3.loadbalancer;

import dto.ClientRequest;
import dto.ServerResponse;

/** Receives a request from LoadBalancerController and reroutes it to an available WebAPIServer
 * Handles all business logic for the LoadBalancer **/
public interface ILoadBalancerService {
    ServerResponse handleClientRequest(ClientRequest request);
}
