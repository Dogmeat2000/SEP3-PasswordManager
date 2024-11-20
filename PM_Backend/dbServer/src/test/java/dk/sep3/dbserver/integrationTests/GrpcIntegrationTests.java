package dk.sep3.dbserver.integrationTests;

import dk.sep3.dbserver.DbServerApplication;
import dk.sep3.dbserver.grpc.factories.LoginEntryDTOGrpcFactory;
import dk.sep3.dbserver.grpc.factories.MasterUserDTOGrpcFactory;
import dk.sep3.dbserver.grpc.service.DbServerPmGrpcServiceImpl;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryServiceImpl;
import grpc.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)  // Gives access to extended testing functionality
@SpringBootTest (
    classes = {
    DbServerApplication.class,
    TestDataSourceConfig.class}) // Signals to Spring Boot that this is a Spring Boot Test and defines which spring configs to use!
@TestPropertySource(properties = {
    "grpc.server.port=9090", // Set to an available port for this test class
    "discovery.datasource.enabled=false", // Ensures that the production database is not used directly!
    "userDatabase.datasource.enabled=false" // Ensures that the production database is not used directly!
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Ensures that Mocks are reset after each test, to avoid tests modifying data in shared mocks, that could cause tests to influence each other.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Establishes an InMemory database instead of using the actual Postgresql database, so tests do not disrupt the production database.
public class GrpcIntegrationTests
{
  @MockBean private DiscoveryRepository dbDiscoveryRepository;

  @InjectMocks private DbServerPmGrpcServiceImpl passwordManagerGrpcService;
  @InjectMocks private DiscoveryRepositoryServiceImpl discoveryRepositoryService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private ManagedChannel channel;
  private PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub passwordManagerStub;

  @BeforeEach
  public void setUp() {
    // Start up a gRPC client channel:
    channel = ManagedChannelBuilder.forAddress("localhost", 9090)
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
    assertTrue(passwordEncoder.matches("TestPassword1", response.getMasterUser().getMasterPassword()));
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

    passwordManagerStub.handleRequest(request); // Add a user to the database, that we can attempt to read.



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
    assertTrue(passwordEncoder.matches("TestPassword2", response.getMasterUser().getMasterPassword()));
    assertEquals(200, response.getStatusCode());
  }


  @Test
  public void testHandleGenericRequest_ReadLoginEntries_ReturnsStatusCode200() {
    // Arrange: Build the MasterUserDTO to Read:
    MasterUserDTO masterUserDTO = MasterUserDTOGrpcFactory.buildGrpcMasterUserDTO(0, "TestUser", "TestPassword");

    // Add the MasterUser to the database, so we have something to read from there:
    GenericRequest request = GenericRequest.newBuilder()
        .setRequestType("CreateMasterUser")
        .setMasterUser(masterUserDTO)
        .build();

    MasterUserDTO createMasterUserResponse = passwordManagerStub.handleRequest(request).getMasterUser();
    masterUserDTO = MasterUserDTOGrpcFactory.buildGrpcMasterUserDTO(createMasterUserResponse.getId(), "TestUser", "TestPassword");

    // Add a few login entries to this Master User:
    LoginEntryDTO loginEntryDTO1 = LoginEntryDTOGrpcFactory.buildGrpcLoginEntryDTO(0, "Discord", "testUser1", "test12345678910", "https://discord.com","Other", masterUserDTO.getId());
    GenericRequest request1 = GenericRequest.newBuilder()
        .setRequestType("CreateLoginEntry")
        .setLoginEntry(loginEntryDTO1)
        .build();

    LoginEntryDTO loginEntryDTO2 = LoginEntryDTOGrpcFactory.buildGrpcLoginEntryDTO(0, "Google", "testUser1", "test12345678910", "https://google.com","Other", masterUserDTO.getId());
    GenericRequest request2 = GenericRequest.newBuilder()
        .setRequestType("CreateLoginEntry")
        .setLoginEntry(loginEntryDTO2)
        .build();

    LoginEntryDTO loginEntryDTO3 = LoginEntryDTOGrpcFactory.buildGrpcLoginEntryDTO(0, "Tv2", "testUser1", "test12345678910", "https://tv2.dk","Other", masterUserDTO.getId());
    GenericRequest request3 = GenericRequest.newBuilder()
        .setRequestType("CreateLoginEntry")
        .setLoginEntry(loginEntryDTO3)
        .build();

    loginEntryDTO1 = passwordManagerStub.handleRequest(request1).getLoginEntry();
    loginEntryDTO2 = passwordManagerStub.handleRequest(request2).getLoginEntry();
    loginEntryDTO3 = passwordManagerStub.handleRequest(request3).getLoginEntry();


    // Act:
    // Build the Message that should be simulated as the incoming gRPC message (MasterUserData to read from the database):
    request = GenericRequest.newBuilder()
        .setRequestType("ReadLoginEntries")
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
    assertEquals(3, response.getLoginEntries().getLoginEntriesList().size());
    assertEquals(loginEntryDTO1, response.getLoginEntries().getLoginEntriesList().get(0));
    assertEquals(loginEntryDTO2, response.getLoginEntries().getLoginEntriesList().get(1));
    assertEquals(loginEntryDTO3, response.getLoginEntries().getLoginEntriesList().get(2));
    assertEquals(200, response.getStatusCode());
  }
}
