package dk.sep3.loadbalancer.WebAPIMonitor;

import dk.sep3.webapi.WebAPIServer;
import dk.sep3.webapi.network.RequestHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

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

            // Add a shutdown hook, so this process is closed when the loadbalancer also closes.
            // Otherwise, the webAPI will continue running in the background after exiting IntelliJ.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (process.isAlive()) {process.destroyForcibly();}
            }));

            // waiting for server to start
            TimeUnit.SECONDS.sleep(7);

            return newServer;

        } catch (IOException e) {
            System.err.println("Failed to start new WebAPI server: " + e.getMessage());
            throw new RuntimeException("Failed to start new WebAPI server", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
