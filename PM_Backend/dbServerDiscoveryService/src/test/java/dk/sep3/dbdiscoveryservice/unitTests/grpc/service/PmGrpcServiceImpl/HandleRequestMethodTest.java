package dk.sep3.dbdiscoveryservice.unitTests.grpc.service.PmGrpcServiceImpl;

import dk.sep3.dbserver.DbServerApplication;
import dk.sep3.dbserver.grpc.adapters.commands.GrpcCommandFactory;
import dk.sep3.dbserver.grpc.service.DbServerPmGrpcServiceImpl;
import dk.sep3.dbserver.grpc.service.ServerHealthMonitor;
import dk.sep3.dbserver.model.discoveryService.db_entities.DatabaseServer;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.repositories.PmDb.MasterUserRepository;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryServiceImpl;
import dk.sep3.dbdiscoveryservice.grpc.service.DbDiscoveryServicePmGrpcServiceImpl;
import dk.sep3.dbdiscoveryservice.integrationTests.TestDataSourceConfig;
import dk.sep3.dbdiscoveryservice.service.DatabaseServerMonitor;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.MasterUserDTO;
import grpc.PasswordManagerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(
    classes = {
        DbServerApplication.class,
        TestDataSourceConfig.class})
@TestPropertySource(properties = {
    "grpc.server.port.primary=9110",     // dbServer gRPC server port
    "grpc.server.port.secondary=9111",   // dbDiscoveryService gRPC server port
    "discovery.datasource.enabled=false", // Ensures that the production database is not used directly!
    "userDatabase.datasource.enabled=false" // Ensures that the production database is not used directly!
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class HandleRequestMethodTest
{
  @MockBean private DiscoveryRepository dbDiscoveryRepository; // Required field. Must not be removed despite no apparent usage!
  @MockBean private DiscoveryRepositoryServiceImpl discoveryRepositoryService;
  @MockBean ServerHealthMonitor serverHealthMonitor;
  @MockBean DatabaseServerMonitor databaseServerMonitor;

  @Autowired
  private MasterUserRepository dbMasterUserRepository;

  @Autowired GrpcCommandFactory commandFactory;

  private DbDiscoveryServicePmGrpcServiceImpl discoveryPasswordManagerGrpcService;
  private DbServerPmGrpcServiceImpl dbServerPasswordManagerGrpcService;

  @Autowired Environment environment;

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
    // Set up some test-data for the test database:
    MasterUser masterUser1 = new MasterUser(0, "TestMasterUser1", "ads91234AVA'S7_:&)/(=9");
    MasterUser masterUser2 = new MasterUser(0, "TestMasterUser2", "ads91234AVA'S7_:&)/(=9");
    MasterUser masterUser3 = new MasterUser(0, "TestMasterUser3", "ads91234AVA'S7_:&)/(=9");
    dbMasterUserRepository.save(masterUser1);
    dbMasterUserRepository.save(masterUser2);
    dbMasterUserRepository.save(masterUser3);

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


  // ZERO / Null Tests below:
  @Test public void whenHandleRequest_IsGivenNullRequest_ReturnsGenericResponseWithCode400AndExceptionMsg() {
    // Arrange:
    when(discoveryRepositoryService.getDatabaseServerWithLeastCongestion()).thenReturn(new DatabaseServer("localhost",9090,0));

    // Act:
    GenericResponse response = discoveryPasswordManagerStub.handleRequest(null);

    // Assert:
    assertEquals(400, response.getStatusCode());
    assertNotNull(response.getException().getException());
    assertFalse(response.getException().getException().isEmpty());
    assertFalse(response.getException().getException().isBlank());
  }


  // One tests below:
  @Test public void whenHandleRequest_IsGivenReadMasterUserRequestWithValidMasterUserDTOThatExistsInDB_ReturnsMasterUserReadFromRepository() {
    // Arrange:
    MasterUserDTO readMasterUserDTO = MasterUserDTO.newBuilder().setId(0).setMasterUsername("TestMasterUser1").setMasterPassword("ads91234AVA'S7_:&)/(=9").build();
    GenericRequest request = GenericRequest.newBuilder().setRequestType("ReadMasterUser").setMasterUser(readMasterUserDTO).build();

    // Act:
    GenericResponse response = discoveryPasswordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(200, response.getStatusCode());
    assertNotNull(response.getMasterUser().getMasterUsername());
    assertEquals(1, response.getMasterUser().getId());
    assertEquals("TestMasterUser1", response.getMasterUser().getMasterUsername());
    assertEquals("ads91234AVA'S7_:&)/(=9", response.getMasterUser().getMasterPassword());
  }

  @Test public void whenHandleRequest_IsGivenCreateMasterUserRequestWithValidMasterUserDTOThatExistsInDB_ReturnsCreatedMasterUser() {
    // Arrange:
    MasterUserDTO readMasterUserDTO = MasterUserDTO.newBuilder().setId(0).setMasterUsername("TestMasterUser4").setMasterPassword("ads91234AVA'S7_:&)/(=9").build();
    GenericRequest request = GenericRequest.newBuilder().setRequestType("CreateMasterUser").setMasterUser(readMasterUserDTO).build();

    // Act:
    GenericResponse response = discoveryPasswordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(201, response.getStatusCode());
    assertNotNull(response.getMasterUser().getMasterUsername());
    assertEquals(4, response.getMasterUser().getId());
    assertEquals("TestMasterUser4", response.getMasterUser().getMasterUsername());
    assertEquals("ads91234AVA'S7_:&)/(=9", response.getMasterUser().getMasterPassword());
  }


  // Many tests below:
  // None defined.


  // Interface tests below: Verify interaction with JPA repository Interface
  // None defined. If above tests work, then interaction with interface is working as intended.


  // Exception tests:
  @Test public void whenHandleRequest_IsGivenReadMasterUserRequestWithValidMasterUserDTOThatDoesNotExistInDB_ReturnsGenericResponseWithCode404AndExceptionMsg() {
    // Arrange:
    MasterUserDTO readMasterUserDTO = MasterUserDTO.newBuilder().setId(0).setMasterUsername("TestMasterUser4").setMasterPassword("123456789101213").build();
    GenericRequest request = GenericRequest.newBuilder().setRequestType("ReadMasterUser").setMasterUser(readMasterUserDTO).build();

    // Act:
    GenericResponse response = discoveryPasswordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(404, response.getStatusCode());
    assertNotNull(response.getException().getException());
    assertFalse(response.getException().getException().isEmpty());
    assertFalse(response.getException().getException().isBlank());
  }

  @Test public void whenHandleRequest_IsGivenReadMasterUserRequestWithInvalidMasterUserDTOContainingBlankUsername_ReturnsGenericResponseWithCode400AndExceptionMsg() {
    // Arrange:
    MasterUserDTO readMasterUserDTO = MasterUserDTO.newBuilder().setId(0).setMasterUsername("").setMasterPassword("ads91234AVA'S7_:&)/(=9").build();
    GenericRequest request = GenericRequest.newBuilder().setRequestType("ReadMasterUser").setMasterUser(readMasterUserDTO).build();

    // Act:
    GenericResponse response = discoveryPasswordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(400, response.getStatusCode());
    assertNotNull(response.getException().getException());
    assertFalse(response.getException().getException().isEmpty());
    assertFalse(response.getException().getException().isBlank());
  }

  @Test public void whenHandleRequest_IsGivenReadMasterUserRequestWithInvalidMasterUserDTOContainingBlankPassword_ReturnsGenericResponseWithCode400AndExceptionMsg() {
    // Arrange:
    MasterUserDTO readMasterUserDTO = MasterUserDTO.newBuilder().setId(0).setMasterUsername("TestMasterUser1").setMasterPassword("").build();
    GenericRequest request = GenericRequest.newBuilder().setRequestType("ReadMasterUser").setMasterUser(readMasterUserDTO).build();

    // Act:
    GenericResponse response = discoveryPasswordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(400, response.getStatusCode());
    assertNotNull(response.getException().getException());
    assertFalse(response.getException().getException().isEmpty());
    assertFalse(response.getException().getException().isBlank());
  }

  @Test public void whenHandleRequest_IsGivenCreateMasterUserRequestWithMasterUserDTOThatAlreadyExistsInDB_ReturnsGenericResponseWithCode409AndExceptionMsg() {
    // Arrange:
    MasterUserDTO readMasterUserDTO = MasterUserDTO.newBuilder().setId(0).setMasterUsername("TestMasterUser1").setMasterPassword("ads91234AVA'S7_:&)/(=9").build();
    GenericRequest request = GenericRequest.newBuilder().setRequestType("CreateMasterUser").setMasterUser(readMasterUserDTO).build();

    // Act:
    GenericResponse response = discoveryPasswordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(409, response.getStatusCode());
    assertNotNull(response.getException().getException());
    assertFalse(response.getException().getException().isEmpty());
    assertFalse(response.getException().getException().isBlank());
  }

  @Test public void whenHandleRequest_IsGivenInvalidRequest_ReturnsGenericResponseWithCode405AndExceptionMsg() {
    // Arrange:
    GenericRequest request = GenericRequest.newBuilder().setRequestType("invalidRequestForGrpcActionDenmarkErFlot").build();

    // Act:
    GenericResponse response = discoveryPasswordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(405, response.getStatusCode());
    assertNotNull(response.getException().getException());
    assertFalse(response.getException().getException().isEmpty());
    assertFalse(response.getException().getException().isBlank());
  }


  // Simple Scenario:
  // Already tested as part of the 'One' tests.
}
