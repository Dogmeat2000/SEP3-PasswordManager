package dk.sep3.dbdiscoveryservice.integrationTests;

import dk.sep3.dbserver.DbServerApplication;
import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommandFactory;
import dk.sep3.dbserver.grpc.factories.MasterUserDTOGrpcFactory;
import dk.sep3.dbserver.grpc.service.DbServerPmGrpcServiceImpl;
import dk.sep3.dbserver.grpc.service.ServerHealthMonitor;
import dk.sep3.dbdiscoveryservice.grpc.service.DbDiscoveryServicePmGrpcServiceImpl;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryServiceImpl;
import dk.sep3.dbdiscoveryservice.service.DatabaseServerMonitor;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.MasterUserDTO;
import grpc.PasswordManagerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ServerBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import io.grpc.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest (
    classes = {
        DbServerApplication.class,
        TestDataSourceConfig.class})
@TestPropertySource(properties = {
    "grpc.server.port.primary=9105",     // dbServer gRPC server port
    "grpc.server.port.secondary=9106",   // dbDiscoveryService gRPC server port
    "discovery.datasource.enabled=false", // Ensures that the production database is not used directly!
    "userDatabase.datasource.enabled=false" // Ensures that the production database is not used directly!
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Ensures that Mocks are reset after each test, to avoid tests modifying data in shared mocks, that could cause tests to influence each other.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Establishes an InMemory database instead of using the actual Postgresql database, so tests do not disrupt the production database.
public class GrpcIntegrationTests
{
  @MockBean private DiscoveryRepository dbDiscoveryRepository;
  @MockBean private ServerHealthMonitor serverHealthMonitor;
  @MockBean private DatabaseServerMonitor databaseServerMonitor;
  @InjectMocks private DiscoveryRepositoryServiceImpl discoveryRepositoryService;

  @Autowired private GrpcCommandFactory commandFactory;
  @Autowired Environment environment;

  private DbDiscoveryServicePmGrpcServiceImpl discoveryPasswordManagerGrpcService;
  private DbServerPmGrpcServiceImpl dbServerPasswordManagerGrpcService;

  private Server dbServerGrpcServer;
  private Server discoveryGrpcServer;

  private ManagedChannel dbServerChannel;
  private ManagedChannel dbDiscoveryChannel;
  private PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub discoveryPasswordManagerStub;

  @Value("${grpc.server.port.primary}")
  private int dbGrpcServerPort;

  @Value("${grpc.server.port.secondary}")
  private int discoveryGrpcServerPort;

  @BeforeEach
  public void setUp() throws IOException {

    // Find available ports for the two gRPC servers:
    while(isPortInUse(dbGrpcServerPort)){
      dbGrpcServerPort++;
    }

    // Set up and start the first gRPC server on primary port
    dbServerGrpcServer = ServerBuilder.forPort(dbGrpcServerPort)
        .addService(new DbServerPmGrpcServiceImpl(serverHealthMonitor, commandFactory))
        .build()
        .start();

    while(isPortInUse(discoveryGrpcServerPort)){
      discoveryGrpcServerPort++;
    }

    discoveryGrpcServer = ServerBuilder.forPort(discoveryGrpcServerPort)
        .addService(new DbDiscoveryServicePmGrpcServiceImpl(discoveryRepositoryService, databaseServerMonitor, environment)) // Another service implementation
        .build()
        .start();

    // Start up gRPC client channels:
    dbServerChannel = ManagedChannelBuilder.forAddress("localhost", dbGrpcServerPort)
        .usePlaintext()
        .build();

    dbDiscoveryChannel = ManagedChannelBuilder.forAddress("localhost", discoveryGrpcServerPort)
        .usePlaintext()
        .build();

    // Initialize all the @Mock and @InjectMock fields, allowing Spring Boot time to perform its Dependency Injection.
    MockitoAnnotations.openMocks(this);

    discoveryPasswordManagerStub = PasswordManagerServiceGrpc.newBlockingStub(dbServerChannel);
  }

  @AfterEach
  public void tearDown() {
    // Shut down both gRPC servers and channels
    if (dbServerGrpcServer != null) {
      dbServerGrpcServer.shutdownNow();
      try {
        if (!dbServerGrpcServer.awaitTermination(15, TimeUnit.SECONDS)) {
          dbServerGrpcServer.shutdownNow();
        }
      } catch (InterruptedException e) {
        dbServerGrpcServer.shutdownNow();
      }
    }

    if (discoveryGrpcServer != null) {
      discoveryGrpcServer.shutdownNow();
      try {
        if (!discoveryGrpcServer.awaitTermination(15, TimeUnit.SECONDS)) {
          discoveryGrpcServer.shutdownNow();
        }
      } catch (InterruptedException e) {
        discoveryGrpcServer.shutdownNow();
      }
    }

    // Shut down gRPC channels as well
    if (dbServerChannel != null) {
      dbServerChannel.shutdownNow();
    }
    if (dbDiscoveryChannel != null) {
      dbDiscoveryChannel.shutdownNow();
    }

    discoveryPasswordManagerStub = null;
  }

  private boolean isPortInUse(int port) {
    try (ServerSocket socket = new ServerSocket(port)) {
      socket.setReuseAddress(true);
      return false;
    } catch (IOException e) {
      return true;
    }
  }

  @Test
  public void testHandleGenericRequest_CreateMasterUser_ReturnsStatusCode201() {
    // Arrange: Build the MasterUserDTO to register:
    MasterUserDTO masterUserDTO = MasterUserDTOGrpcFactory.buildGrpcMasterUserDTO(0, "TestUser1", "TestPassword1");

    // Build the Message that should be simulated as the incoming gRPC message (MasterUserData to add to database):
    GenericRequest request = GenericRequest.newBuilder()
        .setRequestType("CreateMasterUser")
        .setMasterUser(masterUserDTO)
        .build();


    // Act: Send the gRPC message to the server:
    GenericResponse response;
    try {
      response = discoveryPasswordManagerStub.handleRequest(request);
    } catch (StatusRuntimeException e) {
      // Sets the response to null in case of an error, so we ensure that this test fails.
      response = null;
    }


    // Assert:
    // Verify if gRPC response is correct and if the database is updated:
    // Expected response is a MasterUserDTO with an id of 1 and a statusCode of 201.
    assertNotNull(response); // Ensure that the response is not null
    assertEquals(201, response.getStatusCode());
    assertEquals(1, response.getMasterUser().getId());
    assertEquals("TestUser1", response.getMasterUser().getMasterUsername());
  }


  @Test
  public void testHandleGenericRequest_ReadMasterUser_ReturnsStatusCode200() {
    // Arrange: Build the MasterUserDTO to Read:
    MasterUserDTO masterUserDTO = MasterUserDTOGrpcFactory.buildGrpcMasterUserDTO(0, "TestUser2", "TestPassword2");

    // Add the MasterUser to the database, so we have something to read from there:
    GenericRequest request = GenericRequest.newBuilder()
        .setRequestType("CreateMasterUser")
        .setMasterUser(masterUserDTO)
        .build();

    GenericResponse ignored = discoveryPasswordManagerStub.handleRequest(request); // Add a user to the database, that we can attempt to read.



    // Act:
    // Build the Message that should be simulated as the incoming gRPC message (MasterUserData to read from the database):
    request = GenericRequest.newBuilder()
        .setRequestType("ReadMasterUser")
        .setMasterUser(masterUserDTO)
        .build();

    // Send the gRPC message to the server:
    GenericResponse response;
    try {
      response = discoveryPasswordManagerStub.handleRequest(request);
    } catch (StatusRuntimeException e) {
      // Sets the response to null in case of an error, so we ensure that this test fails.
      response = null;
    }


    // Assert:
    // Verify if gRPC response is correct and if the database is updated:
    // Expected response is a MasterUserDTO with an id of 1 and a statusCode of 201.
    assertNotNull(response); // Ensure that the response is not null
    assertEquals(1, response.getMasterUser().getId());
    assertEquals("TestUser2", response.getMasterUser().getMasterUsername());
    assertEquals(200, response.getStatusCode());

  }

}
