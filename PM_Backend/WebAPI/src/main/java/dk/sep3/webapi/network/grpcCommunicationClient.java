package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;
import dk.sep3.webapi.network.converter.ClientRequestToGrpcConverter;
import dk.sep3.webapi.network.converter.GrpcToServerResponseConverter;
import grpc.*;
import grpc.PasswordManagerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** gRPC communicationClient, responsible for making contact with dbServer and sending the results back through the system
 * Uses Converter class to convert ClientRequest to gRPC's GenericRequest-format **/

@Component
public class grpcCommunicationClient implements CommunicationClient {
    private final ManagedChannel channel;
    private final PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub stub;
    private final ClientRequestToGrpcConverter requestConverter;
    private final GrpcToServerResponseConverter responseConverter;

    public grpcCommunicationClient(ClientRequestToGrpcConverter requestConverter, GrpcToServerResponseConverter responseConverter) {
        this.requestConverter = requestConverter;
        this.responseConverter = responseConverter;
        channel = ManagedChannelBuilder.forAddress("localhost", 8090).usePlaintext().build();
        stub = PasswordManagerServiceGrpc.newBlockingStub(channel);
    }


    @Override
    public ServerResponse callDbServer(ClientRequest request) {
        GenericRequest grpcRequest = requestConverter.convert(request);

        GenericResponse grpcResponse = stub.handleRequest(grpcRequest);

        return responseConverter.convert(grpcResponse);
    }

    @Override
    public void shutdown() {
        channel.shutdown();
    }

}
