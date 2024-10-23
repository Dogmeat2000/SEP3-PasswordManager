package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;
import grpc.UserServiceGrpc;

public interface CommunicationClient {
    UserServiceGrpc.UserServiceBlockingStub getStub();
    void shutdown();
    ServerResponse callDbServer(ClientRequest request);
}
