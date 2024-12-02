package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;

/**
 * Interface for the communication client, defines the contract for communication between ClientRequests and dbServer.
 */
public interface CommunicationClient {
    /**
     * Calls the dbServer to handle the provided client request.
     *
     * @param request The ClientRequest object that needs to be processed by the dbServer.
     * @return A ServerResponse containing the response from the dbServer.
     */
    ServerResponse callDbServer(ClientRequest request);

    /**
     * Shuts down the communication client when it is no longer needed.
     */
    void shutdown();
}
