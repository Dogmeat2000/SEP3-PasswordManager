package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;
import dk.sep3.webapi.network.converter.ClientRequestToGrpcConverter;
import dk.sep3.webapi.network.converter.GrpcToServerResponseConverter;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.PasswordManagerServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * gRPC CommunicationClient, responsible for making contact with dbServer and sending the results back through the system.
 * Uses converter classes to convert ClientRequest to gRPC's GenericRequest format and to convert gRPC responses to ServerResponse.
 */
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


    /**
     * Calls the dbServer using gRPC to handle the provided client request.
     *
     * @param request The ClientRequest object that needs to be processed by the dbServer.
     * @return A ServerResponse object containing the response from the dbServer.
     */
    @Override
    public ServerResponse callDbServer(ClientRequest request) {
        GenericRequest grpcRequest = requestConverter.convert(request);

        System.out.println("GrpcCommunicationClient: callDbServer -> " + grpcRequest.toString());

        GenericResponse grpcResponse = stub.handleRequest(grpcRequest);

        ServerResponse response = responseConverter.convert(grpcResponse);
        System.out.println("grpcCommunicationClient: Response: " + response.getStatusCode());

        return response;
    }

    /**
     * Shuts down the gRPC channel when it is no longer needed.
     */
    @Override
    public void shutdown() {
        grpcConfig.shutdownChannel();
    }

}
