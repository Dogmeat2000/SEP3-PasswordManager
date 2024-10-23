package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;
import grpc.UserServiceGrpc;

/** Interface for the communication client, defines the contract for communication between ClientRequests and dbServer **/
public interface CommunicationClient {
    UserServiceGrpc.UserServiceBlockingStub getStub();

    ServerResponse callDbServer(ClientRequest request);
    void shutdown();
}
