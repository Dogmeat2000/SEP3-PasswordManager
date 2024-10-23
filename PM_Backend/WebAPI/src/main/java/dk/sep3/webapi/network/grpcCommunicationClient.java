package dk.sep3.webapi.network;

import common.ServerResponse;
import common.dto.MasterUserDTO;
import common.requests.ClientRequest;
import grpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class grpcCommunicationClient implements CommunicationClient {
    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub stub;

    public grpcCommunicationClient(@Value("${grpc.server.host}")String host, @Value("${grpc.server.port}") int port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = UserServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public UserServiceGrpc.UserServiceBlockingStub getStub() {
        return stub;
    }

    @Override
    public void shutdown() {
        channel.shutdown();
    }

    @Override
    public ServerResponse callDbServer(ClientRequest request) {
        String requestType = request.getRequestType();

        // Depending on the request type, call the corresponding gRPC method
        switch (requestType) {
            case "GetMasterUser":
                // Convert ClientRequest to gRPC request and call dbServer
                MasterUserDTO masterUserDTO = (MasterUserDTO) request.getDTO();
                // Example conversion logic to gRPC request
                // grpcRequest = convertToGrpc(masterUserDTO)
                return getMasterUser(masterUserDTO);

            case "CreateMasterUser":
                MasterUserDTO createDTO = (MasterUserDTO) request.getDTO();
                return createMasterUser(createDTO);

            case "GetLoginEntries":
                int masterUserId = ((MasterUserDTO) request.getDTO()).getId();
                return getLoginEntries(masterUserId);

            default:
                throw new UnsupportedOperationException("Unknown request type: " + requestType);
        }
    }

    private ServerResponse getMasterUser(MasterUserDTO masterUserDTO) {
        // Implement gRPC logic for getMasterUser
        return new ServerResponse(); // Placeholder
    }

    private ServerResponse createMasterUser(MasterUserDTO masterUserDTO) {
        // Implement gRPC logic for createMasterUser
        return new ServerResponse(); // Placeholder
    }

    private ServerResponse getLoginEntries(int masterUserId) {
        // Implement gRPC logic for getLoginEntries
        return new ServerResponse(); // Placeholder
    }
}
