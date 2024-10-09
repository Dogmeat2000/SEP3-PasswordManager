package dk.sep3.passwordmanager.loadBalancer.service;

import dk.sep3.passwordmanager.loadBalancer.dto.ClientRequest;

// Interface for handling and controlling client requests and sending them to the dbServer
public interface ILoadBalancerService {
    void handleLoad(ClientRequest request);
    int getCurrentLoad();
}
