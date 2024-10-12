package dk.sep3.dbserver.integrationTests;

import dk.sep3.dbserver.DbServerApplication;
import dk.sep3.dbserver.model.passwordManager.db_entities.User;
import dk.sep3.dbserver.grpc.adapters.grpc_to_java.UserDataToUserEntity;
import dk.sep3.dbserver.grpc.service.UserGrpcServiceImpl;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.repositories.passwordManagerDb.UserRepository;
import dk.sep3.dbserver.service.discoveryService.DiscoveryRepositoryServiceImpl;
import grpc.UserData;
import grpc.UserNameAndPswd;
import grpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)  // Gives access to extended testing functionality
@SpringBootTest (
    classes = {
    DbServerApplication.class,
    TestDataSourceConfig.class}) // Signals to Spring Boot that this is a Spring Boot Test and defines which spring configs to use!
@TestPropertySource(properties = "discovery.datasource.enabled=false") // Ensures that the production database is not used directly!
@TestPropertySource(properties = "userDatabase.datasource.enabled=false") // Ensures that the production database is not used directly!
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Ensures that Mocks are reset after each test, to avoid tests modifying data in shared mocks, that could cause tests to influence each other.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Establishes an InMemory database instead of using the actual Postgresql database, so tests do not disrupt the production database.
public class UserGrpcIntegrationTest
{
  @MockBean private UserRepository userRepository; // Signals that this is a "fake" Spring Boot bean.
  @MockBean private DiscoveryRepository discoveryRepository; // Included to prevent spring errors while doing dependency injection.

  @InjectMocks private UserGrpcServiceImpl userGrpcService; // The gRPC service being tested. It is auto-injected into the test using dependency injection.
  @InjectMocks private DiscoveryRepositoryServiceImpl discoveryRepositoryService; // Included to prevent spring errors while doing dependency injection.

  private ManagedChannel channel;
  private UserServiceGrpc.UserServiceBlockingStub blockingStub;

  @BeforeEach
  public void setUp() {
    // Start up a gRPC client channel:
    channel = ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext()
        .build();

    blockingStub = UserServiceGrpc.newBlockingStub(channel);

    // Initialize all the @Mock and @InjectMock fields, allowing Spring Boot time to perform its Dependency Injection.
    MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() {
    // Tear down the gRPC client channel:
    channel.shutdownNow();
    channel = null;

    blockingStub = null;
  }


  @Test
  @Transactional
  public void testRegisterUserInDatabaseWithGrpc() {
    // Arrange:
    // Build the Message that should be simulated as the incoming gRPC message (UserData to add to database):
    UserData request = UserData.newBuilder()
        .setUsername("Test User")
        .setEncryptedPassword("HAGHSN1328961BHGNHJAKFS28476293")
        .build();

    // Build a reference Entity, simulating what would be saved in the database
    User user = UserDataToUserEntity.convertToUserEntity(request);
    user.setId(1); // Simulate the database using SERIAL to generate a unique id.

    // Alert Mockito to "mock" the behavior of userRepository when its save method is called,
    // allowing it to simulate database interactions instead of affecting the live production database.
    when(userRepository.save(any(User.class))).thenReturn(user);

    // Act:
    // Send the gRPC message to the server:
    UserData response;
    try {
      response = blockingStub.registerUser(request); // Simulate gRPC call to database server
    } catch (StatusRuntimeException e) {
      // Sets the response to null in case of an error, so we ensure that this test fails.
      response = null;
    }


    // Assert:
    // Verify if gRPC response is correct and if the database is updated:
    assertNotNull(response); // Ensure that the response is not null
    assertEquals(1, response.getId());
    assertEquals("Test User", response.getUsername());
    assertEquals("HAGHSN1328961BHGNHJAKFS28476293", response.getEncryptedPassword());

    // Call Mockito and check that the previously set when() statement was called at least 1 time,
    // to ensure that database interaction was actually simulated here.
    verify(userRepository, times(1)).save(any(User.class));
  }


  @Test
  public void testFetchingUserFromDatabaseWithGrpc() {
    // Arrange:
    // Build the Message that should be simulated as the incoming gRPC message (To fetch user1 from the database):
    UserNameAndPswd request = UserNameAndPswd.newBuilder()
        .setUsername("User1")
        .setEncryptedPassword("HLKNAFSDYH9407219384")
        .build();

    // Simulate what the database/repository should return, when database server fetches user1
    List<User> mockUsers = new ArrayList<>();
    mockUsers.add(new User(1, "User1", "HLKNAFSDYH9407219384"));

    // Alert Mockito to "mock" the behavior of userRepository when its findByUsernameAndEncryptedPassword method is called,
    // allowing it to simulate database interactions instead of affecting the live production database.
    when(userRepository.findByUsernameAndEncryptedPassword("User1", "HLKNAFSDYH9407219384")).thenReturn(mockUsers);

    // Act:
    // Send the gRPC message to the server:
    UserData response;
    try {
      response = blockingStub.getUser(request); // Simulate gRPC call to database server
    } catch (StatusRuntimeException e) {
      // Sets the response to null in case of an error, so we ensure that this test fails.
      response = null;
    }

    // Assert:
    // Verify if gRPC response is correct and if the database is updated:
    assertNotNull(response); // Ensure that the response is not null
    assertEquals(1, response.getId());
    assertEquals("User1", response.getUsername());
    assertEquals("HLKNAFSDYH9407219384", response.getEncryptedPassword());

    // Call Mockito and check that the previously set when() statement was called at least 1 time,
    // to ensure that database interaction was actually simulated here.
    verify(userRepository, times(1)).findByUsernameAndEncryptedPassword("User1", "HLKNAFSDYH9407219384");
  }


  @Test
  public void testFetchingNonExistingUserFromDatabaseWithGrpcThrowsNotFoundException() {
    // Arrange:
    // Build the Message that should be simulated as the incoming gRPC message (To fetch user1 from the database):
    UserNameAndPswd request = UserNameAndPswd.newBuilder()
        .setUsername("User1")
        .setEncryptedPassword("HLKNAFSDYH9407219384")
        .build();

    // Alert Mockito to "mock" the behavior of userRepository when its findById method is called,
    // allowing it to simulate database interactions instead of affecting the live production database.
    when(userRepository.findByUsernameAndEncryptedPassword("User1", "HLKNAFSDYH9407219384")).thenReturn(new ArrayList<>());

    // Act:
    // Send the gRPC message to the server:
    UserData response;
    Status errorStatus = null;
    try {
      response = blockingStub.getUser(request); // Simulate gRPC call to database server
    } catch (StatusRuntimeException e) {
      // Sets the response to null in case of an error, so we ensure that this test fails.
      errorStatus = e.getStatus();
      response = null;
    }

    // Assert:
    // Verify if gRPC response is correct and if the database is updated:
    assertNull(response); // Ensure that the response is null
    assertEquals(Status.NOT_FOUND.getCode(), errorStatus.getCode());

    // Call Mockito and check that the previously set when() statement was called at least 1 time,
    // to ensure that database interaction was actually simulated here.
    verify(userRepository, times(1)).findByUsernameAndEncryptedPassword("User1", "HLKNAFSDYH9407219384");
  }
}
