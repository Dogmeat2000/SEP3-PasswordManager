package dk.sep3.dbserverdiscoveryservice.integrationTests;

import dk.sep3.dbserver.DbServerApplication;
import dk.sep3.dbserver.grpc.factories.MasterUserDTOGrpcFactory;
import dk.sep3.dbserverdiscoveryservice.grpc.service.PasswordManagerGrpcServiceImpl;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryServiceImpl;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.MasterUserDTO;
import grpc.PasswordManagerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest (
    classes = {
    DbServerApplication.class,
    TestDataSourceConfig.class})
@TestPropertySource(properties = "discovery.datasource.enabled=false") // Ensures that the production database is not used directly!
@TestPropertySource(properties = "userDatabase.datasource.enabled=false") // Ensures that the production database is not used directly!
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Ensures that Mocks are reset after each test, to avoid tests modifying data in shared mocks, that could cause tests to influence each other.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Establishes an InMemory database instead of using the actual Postgresql database, so tests do not disrupt the production database.
public class GrpcIntegrationTests
{
  @MockBean private DiscoveryRepository dbDiscoveryRepository;

  @InjectMocks private PasswordManagerGrpcServiceImpl passwordManagerGrpcService;
  @InjectMocks private DiscoveryRepositoryServiceImpl discoveryRepositoryService;

  private ManagedChannel channel;
  private PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub passwordManagerStub;

  @BeforeEach
  public void setUp() {
    // Start up a gRPC client channel:
    channel = ManagedChannelBuilder.forAddress("localhost", 9001)
        .usePlaintext()
        .build();

    passwordManagerStub = PasswordManagerServiceGrpc.newBlockingStub(channel);

    // Initialize all the @Mock and @InjectMock fields, allowing Spring Boot time to perform its Dependency Injection.
    MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() {
    // Tear down the gRPC client channel:
    channel.shutdownNow();
    channel = null;

    passwordManagerStub = null;
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
      response = passwordManagerStub.handleRequest(request);
    } catch (StatusRuntimeException e) {
      // Sets the response to null in case of an error, so we ensure that this test fails.
      response = null;
    }


    // Assert:
    // Verify if gRPC response is correct and if the database is updated:
    // Expected response is a MasterUserDTO with an id of 1 and a statusCode of 201.
    assertNotNull(response); // Ensure that the response is not null
    assertEquals(1, response.getMasterUser().getId());
    assertEquals("TestUser1", response.getMasterUser().getMasterUsername());
    assertEquals("TestPassword1", response.getMasterUser().getMasterPassword());
    assertEquals(201, response.getStatusCode());
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

    GenericResponse ignored = passwordManagerStub.handleRequest(request); // Add a user to the database, that we can attempt to read.



    // Act:
    // Build the Message that should be simulated as the incoming gRPC message (MasterUserData to read from the database):
    request = GenericRequest.newBuilder()
        .setRequestType("ReadMasterUser")
        .setMasterUser(masterUserDTO)
        .build();

    // Send the gRPC message to the server:
    GenericResponse response;
    try {
      response = passwordManagerStub.handleRequest(request);
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
    assertEquals("TestPassword2", response.getMasterUser().getMasterPassword());
    assertEquals(200, response.getStatusCode());

  }

}
