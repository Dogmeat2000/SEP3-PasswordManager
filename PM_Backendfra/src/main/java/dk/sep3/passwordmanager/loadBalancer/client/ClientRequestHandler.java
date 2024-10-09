package dk.sep3.passwordmanager.loadBalancer.client;

import dk.sep3.passwordmanager.loadBalancer.dto.ClientRequest;

// Implementation to handle client requests
public class ClientRequestHandler {
    public void sendSavePasswordRequest(String username, String password) {
        // TODO: Implement the request sending logic using gRPC or REST to load balancer
        System.out.println("Sending save request for username: " + username);
    }

    public String sendGetPasswordRequest(String username) {
        // TODO: Implement request sending logic to load balancer
        System.out.println("Sending get request for username: " + username);
        return "encrypted-password";
    }
}
