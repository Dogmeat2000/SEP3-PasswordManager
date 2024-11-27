package dk.sep3.dbserver.unitTests.grpc.service.PmGrpcServiceImpl;

import dk.sep3.dbserver.DbServerApplication;
import dk.sep3.dbserver.grpc.service.DbServerPmGrpcServiceImpl;
import dk.sep3.dbserver.integrationTests.TestDataSourceConfig;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.repositories.PmDb.MasterUserRepository;
import grpc.GenericRequest;
import grpc.GenericResponse;
import grpc.MasterUserDTO;
import grpc.PasswordManagerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@ExtendWith(MockitoExtension.class)
@SpringBootTest(
    classes = {
        DbServerApplication.class,
        TestDataSourceConfig.class})
@TestPropertySource(properties = {
    "grpc.server.port=9091", // Set to an available port for this test class
    "discovery.datasource.enabled=false", // Ensures that the production database is not used directly!
    "userDatabase.datasource.enabled=false" // Ensures that the production database is not used directly!
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class HandleRequestMethodTest
{
  @MockBean private DiscoveryRepository dbDiscoveryRepository; // Required field. Must not be removed despite no apparent usage!

  @Autowired
  private MasterUserRepository dbMasterUserRepository;

  @Autowired
  private DbServerPmGrpcServiceImpl passwordMngrGrpcService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private ManagedChannel channel;
  private PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub passwordManagerStub;
  private AutoCloseable closeable;

  @BeforeEach
  public void setUp() {
    // Initialize all the @Mock and @InjectMock fields, allowing Spring Boot time to perform its Dependency Injection.
    closeable = MockitoAnnotations.openMocks(this);

    // Set up some test-data for the test database:
    MasterUser masterUser1 = new MasterUser(0, "TestMasterUser1", passwordEncoder.encode("ads91234AVA'S7_:&)/(=9"));
    MasterUser masterUser2 = new MasterUser(0, "TestMasterUser2", passwordEncoder.encode("ads91234AVA'S7_:&)/(=9"));
    MasterUser masterUser3 = new MasterUser(0, "TestMasterUser3", passwordEncoder.encode("ads91234AVA'S7_:&)/(=9"));
    dbMasterUserRepository.save(masterUser1);
    dbMasterUserRepository.save(masterUser2);
    dbMasterUserRepository.save(masterUser3);

    // Start up a gRPC client channel:
    channel = ManagedChannelBuilder.forAddress("localhost", 9091)
        .usePlaintext()
        .build();

    passwordManagerStub = PasswordManagerServiceGrpc.newBlockingStub(channel);
  }

  @AfterEach
  public void tearDown() {
    // Tear down the gRPC client channel:
    channel.shutdownNow();
    channel = null;
    passwordManagerStub = null;

    // Close the Mockito Injections:
    try {
      closeable.close();
    } catch (Exception ignored) {}
  }


  // ZERO / Null Tests below:
  @Test public void whenHandleRequest_IsGivenNullRequest_ReturnsGenericResponseWithCode400AndExceptionMsg() {
    // Act:
    GenericResponse response = passwordManagerStub.handleRequest(null);

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
    GenericResponse response = passwordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(200, response.getStatusCode());
    assertNotNull(response.getMasterUser().getMasterUsername());
    assertEquals(1, response.getMasterUser().getId());
    assertEquals("TestMasterUser1", response.getMasterUser().getMasterUsername());
    assertTrue(passwordEncoder.matches("ads91234AVA'S7_:&)/(=9", response.getMasterUser().getMasterPassword()));
  }

  @Test public void whenHandleRequest_IsGivenCreateMasterUserRequestWithValidMasterUserDTOThatExistsInDB_ReturnsCreatedMasterUser() {
    // Arrange:
    MasterUserDTO readMasterUserDTO = MasterUserDTO.newBuilder().setId(0).setMasterUsername("TestMasterUser4").setMasterPassword("ads91234AVA'S7_:&)/(=9").build();
    GenericRequest request = GenericRequest.newBuilder().setRequestType("CreateMasterUser").setMasterUser(readMasterUserDTO).build();

    // Act:
    GenericResponse response = passwordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(201, response.getStatusCode());
    assertNotNull(response.getMasterUser().getMasterUsername());
    assertEquals(4, response.getMasterUser().getId());
    assertEquals("TestMasterUser4", response.getMasterUser().getMasterUsername());
    assertTrue(passwordEncoder.matches("ads91234AVA'S7_:&)/(=9", response.getMasterUser().getMasterPassword()));
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
    GenericResponse response = passwordManagerStub.handleRequest(request);

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
    GenericResponse response = passwordManagerStub.handleRequest(request);

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
    GenericResponse response = passwordManagerStub.handleRequest(request);

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
    GenericResponse response = passwordManagerStub.handleRequest(request);

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
    GenericResponse response = passwordManagerStub.handleRequest(request);

    // Assert:
    assertEquals(405, response.getStatusCode());
    assertNotNull(response.getException().getException());
    assertFalse(response.getException().getException().isEmpty());
    assertFalse(response.getException().getException().isBlank());
  }


  // Simple Scenario:
  // Already tested as part of the 'One' tests.
}
