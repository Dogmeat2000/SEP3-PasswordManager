package dk.sep3.dbserverdiscoveryservice.service;

import dk.sep3.dbserver.model.discoveryService.db_entities.DatabaseServer;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryService;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


@Service
public class DatabaseServerMonitor
{
  private final int threadCycleSleepTimeInMs = 4000; // 1 Second.
  private final int serverTimeoutInSeconds = 30; // 30 Seconds.
  private final DiscoveryRepositoryService discoveryRepositoryService;
  private final Map<String, List<Integer>> validHostsAndPort;
  private final List<String> validHosts;
  private static final Logger logger = LoggerFactory.getLogger(DatabaseServerMonitor.class);

  @Value("${portMin}")
  private int portMin;

  @Value("${portMax}")
  private int portMax;

  public DatabaseServerMonitor(DiscoveryRepositoryService discoveryRepositoryService){
    this.discoveryRepositoryService = discoveryRepositoryService;
    validHosts = new ArrayList<>();
    validHostsAndPort = new HashMap<>();
  }


  private void initialize() {
    // Define valid host addresses:
    validHosts.add("localhost");
    List<Integer> validPorts = new ArrayList<>();

    // Gather valid ports:
    for (int port = portMin; port < portMax; port++) {
      validPorts.add(port);
    }

    // Combine valid host addresses and ports:
    for (String host : validHosts) {
      validHostsAndPort.put(host, validPorts);
    }
  }


  public void startService() {
    initialize();
    logger.info("DatabaseServer Discovery Service Health Monitor is running!");
    // Run forever
    while(true) {
      // Check if there are any database servers running:
      DatabaseServer server;
      try {
        server = discoveryRepositoryService.getDatabaseServerWithOldestPing();
      } catch (NotFoundInDBException e) {
        logger.error("Unable to fetch Database with highest ping in DB, Reason: {}",e.getMessage());
        server = null;
      }

      if(server == null){
        // If not, Launch a new database server
        logger.info("No Database Servers running. Booting up a gRPC server...");
        Thread newThread = new Thread(() -> launchNewDatabaseServer());
        newThread.setDaemon(true);
        newThread.start();
      } else {
        // Check if the oldest ping is older than the timeout limit:
        Duration timeDifference = Duration.between(Instant.now(), server.getLastPing()); //Gets time between now and the timestamp in the database.

        logger.info("Time difference between Discovery service and last pinged server is: {} seconds", Math.abs(timeDifference.getSeconds()));
        if(Math.abs(timeDifference.getSeconds()) > serverTimeoutInSeconds) {
          // This DatabaseServer has passed timeout limit. Remove it from the discovery client:
          discoveryRepositoryService.unregisterDatabaseServer(server);
          logger.info("Found a server that has passed timeout. Removing {} from DatabaseServer DB", server.getHost());
        }
      }

      // Check if least congested server is below 50% capacity, else launch a new Database Server:
      try {
        server = discoveryRepositoryService.getDatabaseServerWithLeastCongestion();
        if(server != null && server.getCongestionPercentage() > 50){
          // Least congested server has reached 50% capacity. Launch another Database Server:
          logger.info("Least congested server has passed congestion limit host:'{}', congestion: '{}'. Launching another server to relieve load", server.getHost(), server.getCongestionPercentage());
          Thread newThread = new Thread(() -> launchNewDatabaseServer());
          newThread.setDaemon(true);
          newThread.start();
        }
      } catch (Exception e) {
        //continue;
      }

      // Sleep for the given cycle time, before checking the next server:
      try {
        Thread.sleep(threadCycleSleepTimeInMs);
      } catch (InterruptedException e) {
        continue;
      }
    }
  }


  // TODO: This is NOT the most elegant method. But it works, for now. Should be refactored!
  private void launchNewDatabaseServer() {
    // Select a free host address and port:
    // TODO: Check which host addresses and ports are already in use, and select free ones
    try {
      String host = validHosts.getFirst();
      // TODO: Currently just gets a random port from the valid list.
      // TODO: This can cause server launched to conflict, but should work for now.
      int port = validHostsAndPort.get(host).get((int) (Math.random()*validHostsAndPort.get(host).size()));
      // Build the command to launch a new instance of the GRPC server, through the dbServer.jar file.
      // TODO: Rewrite, so this can be done over the network. Currently just launches a new instance at localhost (this host).
      ProcessBuilder processBuilder = new ProcessBuilder(
          "java", "-jar", "./PM_Backend/dbServerDiscoveryService/target/libs/dbServer-0.0.1-SNAPSHOT-exe.jar",
          "--db.host=" + host,
          "--db.port=" + port);

      // Start the new gRPC server:
      Process process = processBuilder.start();

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        if (process != null && process.isAlive()) {
          logger.info("Shutting down launched dbServer process...");
          process.destroyForcibly(); // This attempts clean termination
        }
      }));

      // Capture the output from the JAR process
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        logger.info("gRPC Data Server: {}", line);
      }

      // Wait for the process to complete
      int exitCode = process.waitFor();

    } catch (IOException | InterruptedException e) {
      logger.error("Error occurred in separate gRPC server instance. Maybe it crashed? Maybe it never started?");
    } catch (NoSuchElementException e) {
      //continue
    }
  }
}
