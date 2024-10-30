package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;
import dk.sep3.webapi.network.converter.ClientRequestToGrpcConverter;
import dk.sep3.webapi.network.converter.GrpcToServerResponseConverter;
import grpc.*;
import grpc.PasswordManagerServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** gRPC communicationClient, responsible for making contact with dbServer and sending the results back through the system
 * Uses Converter class to convert ClientRequest to gRPC's GenericRequest-format **/

@Component
public class grpcCommunicationClient implements CommunicationClient {
    private final PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub stub;
    private final ClientRequestToGrpcConverter requestConverter;
    private final GrpcToServerResponseConverter responseConverter;
    private final GrpcConfig grpcConfig;

    @Autowired
    public grpcCommunicationClient(PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub stub,
        GrpcConfig grpcConfig,
        ClientRequestToGrpcConverter requestConverter,
        GrpcToServerResponseConverter responseConverter) {

        this.grpcConfig = grpcConfig;
        this.stub = stub;
        this.requestConverter = requestConverter;
        this.responseConverter = responseConverter;
    }


    @Override
    public ServerResponse callDbServer(ClientRequest request) {
        GenericRequest grpcRequest = requestConverter.convert(request);

        GenericResponse grpcResponse = stub.handleRequest(grpcRequest);

        return responseConverter.convert(grpcResponse);
    }

    @Override
    public void shutdown() {
        grpcConfig.shutdownChannel();
    }

}
