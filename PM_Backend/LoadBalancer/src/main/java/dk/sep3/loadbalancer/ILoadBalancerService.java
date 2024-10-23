package dk.sep3.loadbalancer;

import common.ClientRequest;

/** LoadBalancerService selects an available WebAPI server and returns its address **/
public interface ILoadBalancerService {
    String getAvailableWebApiServer(ClientRequest request);
    void handleServerFailure(String serverUrl);
}
