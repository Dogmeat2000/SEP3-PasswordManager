package dk.sep3.loadbalancer.WebAPIMonitor;

import dk.sep3.webapi.WebAPIServer;
import dk.sep3.webapi.network.RequestHandler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

/** Factory for creating new WebAPIServer instances **/
@Component
public class WebAPIServerFactory {

    private final RequestHandler requestHandler;
    private int currentPort = 8081;

    public WebAPIServerFactory(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public WebAPIServer createNewServer() {
        int port = getNextAvailablePort();
        String newServerUrl = "http://localhost:" + port;

        try {
            System.out.println("Starting new WebAPIApplication on port " + port);
            String jarPath = "./PM_Backend/WebAPI/target/WebAPI-1.0-SNAPSHOT-exe.jar";

            ProcessBuilder builder = new ProcessBuilder("java", "-jar", jarPath, "--server.port=" + port);

            builder.redirectErrorStream(true);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process process = builder.start();

            WebAPIServer newServer = new WebAPIServer(requestHandler);
            newServer.setUrl(newServerUrl);
            newServer.setProcess(process); // Storing process for later termination
            return newServer;

        } catch (IOException e) {
            System.err.println("Failed to start new WebAPI server: " + e.getMessage());
            throw new RuntimeException("Failed to start new WebAPI server", e);
        }
    }

    private int getNextAvailablePort() {
        while (true) {
            int port = currentPort++;
            if (isPortAvailable(port)) {
                return port;
            }
        }
    }

    private boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
