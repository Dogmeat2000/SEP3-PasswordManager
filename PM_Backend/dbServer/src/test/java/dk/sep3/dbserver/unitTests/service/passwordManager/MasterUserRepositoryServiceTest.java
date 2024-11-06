package dk.sep3.dbserver.unitTests.service.passwordManager;

import dk.sep3.dbserver.DbServerApplication;
import dk.sep3.dbserver.integrationTests.TestDataSourceConfig;
import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.repositories.passwordManagerDb.MasterUserRepository;
import dk.sep3.dbserver.service.passwordManager.MasterUserRepositoryServiceImpl;
import jakarta.inject.Inject;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertThrows;

/** <p>Defines the automated UnitTests to run on the MasterUserRepositoryService methods.</p>*/
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(
    classes = {
        DbServerApplication.class,
        TestDataSourceConfig.class})
@TestPropertySource(properties = "discovery.datasource.enabled=false") // Ensures that the production database is not used directly!
@TestPropertySource(properties = "userDatabase.datasource.enabled=false") // Ensures that the production database is not used directly!
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class MasterUserRepositoryServiceTest
{
  @MockBean private DiscoveryRepository dbDiscoveryRepository;
  @Inject private MasterUserRepository dbMasterUserRepository;
  @InjectMocks private MasterUserRepositoryServiceImpl dbMasterUserRepositoryService;


  @BeforeEach
  public void setUp() {
    // Initialize all the @Mock and @InjectMock fields, allowing Spring Boot time to perform its Dependency Injection.
    MockitoAnnotations.openMocks(this);


    // Set up some test-data for the test database:
    MasterUser masterUser1 = new MasterUser(1, "TestMasterUser1", "ads91234AVA'S7_:&)/(=9");
    MasterUser masterUser2 = new MasterUser(1, "TestMasterUser2", "ads91234AVA'S7_:&)/(=9");
    MasterUser masterUser3 = new MasterUser(1, "TestMasterUser3", "ads91234AVA'S7_:&)/(=9");
    dbMasterUserRepository.save(masterUser1);
    dbMasterUserRepository.save(masterUser2);
    dbMasterUserRepository.save(masterUser3);
  }

  @AfterEach
  public void tearDown() {
    // Empty
  }


  @Test
  public void whenCreateMasterUser_IsGivenNullMasterUser_ThrowIllegalArgumentException() {
    // Arrange: Empty

    // Act:Empty

    // Assert:
    assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.createMasterUser(null));
  }


}
