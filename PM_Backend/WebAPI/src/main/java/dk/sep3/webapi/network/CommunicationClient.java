package dk.sep3.webapi.network;

import common.ClientRequest;
import common.ServerResponse;

/** Interface for the communication client, defines the contract for communication between ClientRequests and dbServer **/
public interface CommunicationClient {
    ServerResponse callDbServer(ClientRequest request);
    void shutdown();
}
