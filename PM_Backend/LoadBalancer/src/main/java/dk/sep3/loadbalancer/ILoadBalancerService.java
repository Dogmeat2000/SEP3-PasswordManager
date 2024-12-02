package dk.sep3.loadbalancer;

import common.ClientRequest;

/** LoadBalancerService selects an available WebAPI server and returns its address **/
public interface ILoadBalancerService {

    /**
     * Retrieves an available WebAPI server based on the client request.
     *
     * @param request The client request containing information for selecting an available server.
     * @return The URL of an available WebAPI server, or a message indicating that no servers are available.
     */
    String getAvailableWebApiServer(ClientRequest request);

    /**
     * Handles server failure by notifying the monitor about the server that is no longer available.
     *
     * @param serverUrl The URL of the server that has failed.
     */
    void handleServerFailure(String serverUrl);
}
