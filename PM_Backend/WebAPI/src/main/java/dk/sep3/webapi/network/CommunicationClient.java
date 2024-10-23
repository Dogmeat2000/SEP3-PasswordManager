package dk.sep3.webapi.network;

import common.ServerResponse;
import common.dto.MasterUserDTO;
import common.requests.ClientRequest;
import grpc.UserServiceGrpc;

public interface CommunicationClient {
    UserServiceGrpc.UserServiceBlockingStub getStub();
    void shutdown();
    ServerResponse callDbServer(ClientRequest request);
}
