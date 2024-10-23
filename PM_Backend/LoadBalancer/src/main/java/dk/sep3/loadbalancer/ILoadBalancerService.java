package dk.sep3.loadbalancer;

import common.ServerResponse;
import common.requests.ClientRequest;

/** LoadBalancerService selects an available WebAPI server and returns its address **/
public interface ILoadBalancerService {
    String getAvailableWebApiServer(ClientRequest request);
    void handleServerFailure(String serverUrl);
}
