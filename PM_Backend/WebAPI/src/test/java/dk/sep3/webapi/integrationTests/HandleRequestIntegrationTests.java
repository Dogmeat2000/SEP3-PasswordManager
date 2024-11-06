package dk.sep3.webapi.integrationTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.ClientRequest;
import common.ServerResponse;
import common.dto.MasterUserDTO;
import dk.sep3.webapi.network.converter.ClientRequestToGrpcConverter;
import dk.sep3.webapi.network.grpcCommunicationClient;
import grpc.GenericResponse;
import grpc.PasswordManagerServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Ensures that Mocks are reset after each test, to avoid tests modifying data in shared mocks, that could cause tests to influence each other.
public class HandleRequestIntegrationTests
{
  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper; // JSON serializer

  @Autowired
  private ClientRequestToGrpcConverter clientRequestToGrpcConverter;

  @MockBean
  private PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub stub;

  @InjectMocks
  private grpcCommunicationClient grpcClient;

  @LocalServerPort
  private int port;


  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() {
    // Empty
  }


  @Test
  public void testHandleGenericRequest_CreateMasterUser_ReturnsStatusCode201() {
    // Arrange: Build the MasterUserDTO to register:
    MasterUserDTO masterUserDTO = new MasterUserDTO("TestUser1", "TestPassword1");

    // Build the Message that should be simulated as the incoming HTTP message:
    ClientRequest request = new ClientRequest("CreateMasterUser", masterUserDTO);

    try {
      // Serialize as JSON, to simulate the state the request leaves the C# client:
      String requestBody = objectMapper.writeValueAsString(request);

      // Pack the request into an HttpEntity, to simulate receiving an HTTP request from the web.
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

      // Since we cannot test on the full depth of the application, we need to Mock the expected server response
      // and trust that tests in other modules identify any issues belonging to such modules:
      grpc.MasterUserDTO expectedDTO = grpc.MasterUserDTO.newBuilder().setId(1).setMasterUsername("TestUser1").setMasterPassword("TestPassword1").build();
      GenericResponse mockResponse = grpc.GenericResponse.newBuilder().setStatusCode(201).setMasterUser(expectedDTO).build();

      when(stub.handleRequest(clientRequestToGrpcConverter.convert(request))).thenReturn(mockResponse);

      // Act: Send the HTTP message to the server:
      String url = "http://localhost:" + port + "/api/handleRequest";
      ResponseEntity<ServerResponse> response = restTemplate.postForEntity(url, entity, ServerResponse.class);

      // Assert:
      // Verify that the gRPC method was called:
      verify(stub, times(1)).handleRequest(clientRequestToGrpcConverter.convert(request));

      // Ensure that the response is not null
      assertNotNull(response);
      assertNotNull(response.getBody());
      assertNotNull(response.getStatusCode());

      // Ensure that statusCode is CREATED:
      assertEquals(HttpStatus.CREATED, response.getStatusCode());

      // Ensure that we receive a MasterUserDTO instance, with correct values:
      assertThat(response.getBody().getDto()).isInstanceOf(MasterUserDTO.class);
      assertEquals(((MasterUserDTO) response.getBody().getDto()).getMasterUsername(), masterUserDTO.getMasterUsername()); // Ensure that the returned username is the same
      assertEquals(((MasterUserDTO) response.getBody().getDto()).getMasterPassword(), masterUserDTO.getMasterPassword()); // Ensure that the returned password is the same

    } catch (JsonProcessingException e) {
      fail("Unexpected Exception thrown while testing. " + e.getMessage());
    }
  }


  @Test
  public void testHandleGenericRequest_ReadMasterUser_ReturnsStatusCode200() {
    // Arrange: Build the MasterUserDTO to register:
    common.dto.MasterUserDTO masterUserDTO = new MasterUserDTO("TestUser2", "TestPassword2");

    // Build the Message that should be simulated as the incoming HTTP message:
    ClientRequest request = new ClientRequest("ReadMasterUser", masterUserDTO);

    try {
      // Serialize as JSON, to simulate the state the request leaves the C# client:
      String requestBody = objectMapper.writeValueAsString(request);

      // Pack the request into an HttpEntity, to simulate receiving an HTTP request from the web.
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

      // Since we cannot test on the full depth of the application, we need to Mock the expected server response
      // and trust that tests in other modules identify any issues belonging to such modules:
      grpc.MasterUserDTO expectedDTO = grpc.MasterUserDTO.newBuilder().setId(1).setMasterUsername("TestUser2").setMasterPassword("TestPassword2").build();
      GenericResponse mockResponse = grpc.GenericResponse.newBuilder().setStatusCode(200).setMasterUser(expectedDTO).build();

      when(stub.handleRequest(clientRequestToGrpcConverter.convert(request))).thenReturn(mockResponse);

      // Act: Send the HTTP message to the server:
      String url = "http://localhost:" + port + "/api/handleRequest";
      ResponseEntity<ServerResponse> response = restTemplate.postForEntity(url, entity, ServerResponse.class);

      // Assert:
      // Verify that the gRPC connection stub was called once:
      verify(stub, times(1)).handleRequest(clientRequestToGrpcConverter.convert(request));

      // Ensure that the response is not null
      assertNotNull(response);
      assertNotNull(response.getBody());
      assertNotNull(response.getStatusCode());

      // Ensure that statusCode is OK:
      assertEquals(HttpStatus.OK, response.getStatusCode());

      // Ensure that we receive a MasterUserDTO instance, with correct values:
      assertThat(response.getBody().getDto()).isInstanceOf(MasterUserDTO.class);
      assertEquals(((MasterUserDTO) response.getBody().getDto()).getMasterUsername(), masterUserDTO.getMasterUsername()); // Ensure that the returned username is the same
      assertEquals(((MasterUserDTO) response.getBody().getDto()).getMasterPassword(), masterUserDTO.getMasterPassword()); // Ensure that the returned password is the same

    } catch (JsonProcessingException e) {
      fail("Unexpected Exception thrown while testing. " + e.getMessage());
    }
  }

}
