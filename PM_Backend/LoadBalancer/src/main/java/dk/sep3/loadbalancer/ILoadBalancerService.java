package dk.sep3.loadbalancer;


import dto.ClientRequest;

/** Receives a request from LoadBalancerController and reroutes it to an available WebAPIServer
 * Handles all business logic for the LoadBalancer **/
public interface ILoadBalancerService {
    void handleClientRequest(ClientRequest request);
}
