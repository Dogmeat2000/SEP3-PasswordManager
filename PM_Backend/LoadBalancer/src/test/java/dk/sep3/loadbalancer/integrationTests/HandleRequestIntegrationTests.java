package dk.sep3.loadbalancer.integrationTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.ClientRequest;
import common.ServerResponse;
import common.dto.MasterUserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

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
  public void testGetAvailableServer_ReturnsLocalHost_And_LocalPort() {
    try {
      // Serialize as JSON, to simulate the state the request leaves the C# client:
      ClientRequest requestServerAddress = new ClientRequest("GetAvailableServer", null);

      // Pack the request into an HttpEntity, to simulate receiving an HTTP request from the web.
      String requestServerAddressBody = objectMapper.writeValueAsString(requestServerAddress);
      HttpHeaders headersServerAddress = new HttpHeaders();
      headersServerAddress.setContentType(MediaType.APPLICATION_JSON);

      // Since we cannot test on the full depth of the application, we need to Mock the expected server response
      // and trust that tests in other modules identify any issues belonging to such modules:
      HttpEntity<String> entityServerAddress = new HttpEntity<>(requestServerAddressBody, headersServerAddress);

      // Act: Send the HTTP message to the server:

      String urlServerAddress = "http://localhost:" + port + "/loadbalancer/server";
      ResponseEntity<ServerResponse> responseServerAddress = restTemplate.postForEntity(urlServerAddress, entityServerAddress, ServerResponse.class);

      // Assert:
      // Ensure that the response is not null
      assertNotNull(responseServerAddress);
      assertNotNull(responseServerAddress.getBody());
      assertNotNull(responseServerAddress.getStatusCode());

      // Ensure that statusCode is CREATED:
      assertEquals("http://localhost:8081", responseServerAddress.getBody().getMessage());

    } catch (JsonProcessingException e) {
      fail("Unexpected Exception thrown while testing. " + e.getMessage());
    }
  }

}
