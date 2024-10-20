package dk.sep3.webapi;

import dto.ClientRequest;
import dto.GetUserClientRequest;
import dto.ServerResponse;
import grpc.UserData;
import grpc.UserNameAndPswd;
import grpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/** Handles requests from clients and exposes the API-methods to interact with the dbServer **/
public class WebAPIServer {
    private int currentLoad;
    private final int MAX_LOAD = 3;
    private boolean available;

    // gRPC Connection attributes
    private String host = "localhost";
    private int port = 8090;
    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public WebAPIServer() {
        this.currentLoad = 0;
        this.available = true;

        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.userServiceStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public ServerResponse handleRequest(GetUserClientRequest request) {
        if (!isAvailable()) {
            return new ServerResponse("Server is overloaded, cannot handle more requests.", 503);
        }

        try {
            currentLoad++;

            UserNameAndPswd grpcRequest = UserNameAndPswd.newBuilder()
                    .setUsername(request.getUsername())
                    .setEncryptedPassword(request.getPassword())
                    .build();

            UserData grpcResponse = userServiceStub.getUser(grpcRequest);

            // Return the response to the client
            ServerResponse response = new ServerResponse("Request handled successfully", 200);
            finishRequest();
            return response;
        } catch (StatusRuntimeException e) {
            return new ServerResponse("Error: " + e.getStatus(), 500);
        }
    }

    private void finishRequest() {
        System.out.println("Finished request. Reducing load.");
        resetLoad();
    }

    public boolean isActive() {
        return true;
    }

    public boolean isAvailable() {
        return available = currentLoad < MAX_LOAD;
    }

    public void resetLoad() {
        if (currentLoad > 0) {
            currentLoad--;
        }
        available = true;
    }

    public void shutdown() {
        channel.shutdown();
    }
}
