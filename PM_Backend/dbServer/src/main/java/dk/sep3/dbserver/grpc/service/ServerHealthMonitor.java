package dk.sep3.dbserver.grpc.service;

import dk.sep3.dbserver.model.discoveryService.db_entities.DatabaseServer;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.MeterNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;


/**<p> Class is responsible for the high level Server Health Monitoring.
 * It continuously evaluates local database server health and updates the central database server
 * repository with the most updated health metrics, so the database discovery service always can
 * choose the least congested database servers to redirect gRPC calls to.</p>*/
@Service
public class ServerHealthMonitor
{
  private final DiscoveryRepositoryService discoveryRepositoryService;
  private static final Logger logger = LoggerFactory.getLogger(ServerHealthMonitor.class);
  private final int threadCycleSleepTimeInMs = 7500; // 7.5 Seconds.
  private DatabaseServer thisServer;

  @Autowired
  MeterRegistry meterRegistry;

  @Value("${grpc.maxNumberOfConcurrentConnections}")
  private String maxNumberOfConcurrentConnections;

  @Value("${grpc.server.port}")
  private String port;

  private int congestionPercentage = 0;

  @Autowired
  public ServerHealthMonitor(DiscoveryRepositoryService discoveryRepositoryService){
    this.discoveryRepositoryService = discoveryRepositoryService;
  }


  public void startService() {
    logger.info("Server Health Monitor is running!");
    // Run forever
    while(true) {
      // Register local servers connection details:
      setThisServer();

      // Calculate congestion state:
      congestionPercentage = calculateCongestionPercentage(meterRegistry);

      // Register with Database Discovery Service:
      try {
        thisServer = discoveryRepositoryService.registerDatabaseServer(thisServer);
        logger.info("Server was logged to discovery service with IP={} and ID={}", thisServer.getHost(), thisServer.getId());
      } catch (Exception e) {
        // Error occurred while registering. Keep trying.
        logger.error("Server Health Monitor was unable to register with database server discovery service, reason {}", e.getMessage());
      }

      // Sleep for the given cycle time, before checking the next server:
      try {
        Thread.sleep(threadCycleSleepTimeInMs);
      } catch (InterruptedException e) {
        // Keep trying.
        continue;
      }
    }
  }


  private void setThisServer() {
    String host;
    long id = 0;

    if(thisServer != null)
      id = thisServer.getId();

    try {
      host = getExternalIp();
    } catch (Exception e) {
      host = "localhost";
    }
    logger.info("Creating local Server object with host='{}' and port='{}'", host, port);
    try {
      thisServer = new DatabaseServer(host, Integer.parseInt(port), congestionPercentage);
      thisServer.setId(id);
    } catch (Exception e) {
      logger.error("Failed to properly create local Server object with host='{}' and port='{}'. Reason: {}", host, port, e.getMessage());
      thisServer = null;
    }
  }


  private String getExternalIp() throws Exception {
    String ipServiceUrl = "https://checkip.amazonaws.com/";
    URI uri = new URI(ipServiceUrl);
    URL url = uri.toURL();

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String publicIp = in.readLine();  // Get the response (The external IP)
    in.close();

    //TODO: Unquote this return value, once host locations have been set up with proper port-forwarding. Return localhost until further notice:
    //return publicIp;
    return "localhost";
  }


  private int calculateCongestionPercentage(MeterRegistry meterRegistry){
    try {
      // Retrieve the gauge value by its name. Defined in ConnectionTracker class!
      double activeConnections = meterRegistry.get("grpc.server.active.connections").gauge().value();

      //Calculate congestion percentage:
      return (int) (activeConnections / Integer.parseInt(maxNumberOfConcurrentConnections)) * 100;

    } catch (MeterNotFoundException | NullPointerException e) {
      logger.error("Server Health Monitor failed to calculate congestion percentage {}", e.getMessage());
      return -1;
    }
  }
}
