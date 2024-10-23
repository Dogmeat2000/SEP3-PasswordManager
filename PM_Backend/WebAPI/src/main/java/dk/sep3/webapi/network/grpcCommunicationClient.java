package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.MasterUserDTO;
import grpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** gRPC communication client, responsible for making contact with dbServer and sending the results back through the system
 * Uses Converter class to convert ClientRequest to gRPC's GenericRequest-format **/
@Component
public class grpcCommunicationClient implements CommunicationClient {
    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub stub;
    private final ClientRequestToGrpcConverter requestConverter;
    private final GrpcToServerResponseConverter responseConverter;

    public grpcCommunicationClient(@Value("${grpc.server.host}")String host, @Value("${grpc.server.port}") int port, ClientRequestToGrpcConverter requestConverter, GrpcToServerResponseConverter responseConverter) {
        this.requestConverter = requestConverter;
        this.responseConverter = responseConverter;
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = UserServiceGrpc.newBlockingStub(channel);
    }


    @Override
    public ServerResponse callDbServer(ClientRequest request) {
        GenericRequest grpcRequest = requestConverter.convert(request);

        GenericResponse grpcResponse = stub.handleRequest(grpcRequest);

        return responseConverter.convert(grpcResponse);
    }

    @Override
    public UserServiceGrpc.UserServiceBlockingStub getStub() {
        return stub;
    }

    @Override
    public void shutdown() {
        channel.shutdown();
    }

}
