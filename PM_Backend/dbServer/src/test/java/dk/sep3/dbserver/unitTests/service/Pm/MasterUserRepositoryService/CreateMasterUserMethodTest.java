package dk.sep3.dbserver.unitTests.service.Pm.MasterUserRepositoryService;

import dk.sep3.dbserver.DbServerApplication;
import dk.sep3.dbserver.integrationTests.TestDataSourceConfig;
import dk.sep3.dbserver.model.Pm.db_entities.MasterUser;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.repositories.PmDb.MasterUserRepository;
import dk.sep3.dbserver.service.exceptions.DuplicateDbEntryException;
import dk.sep3.dbserver.service.Pm.MasterUserRepositoryServiceImpl;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/** <p>Defines the automated UnitTests to run on the MasterUserRepositoryService methods.</p>*/
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(
    classes = {
        DbServerApplication.class,
        TestDataSourceConfig.class})
@TestPropertySource(properties = {
    "discovery.datasource.enabled=false", // Ensures that the production database is not used directly!
    "userDatabase.datasource.enabled=false" // Ensures that the production database is not used directly!
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CreateMasterUserMethodTest
{
  @MockBean private DiscoveryRepository dbDiscoveryRepository; // Required field. Must not be removed despite no apparent usage!

  @Autowired
  private MasterUserRepository dbMasterUserRepository;

  @Autowired
  private MasterUserRepositoryServiceImpl dbMasterUserRepositoryService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    // Initialize all the @Mock and @InjectMock fields, allowing Spring Boot time to perform its Dependency Injection.
    MockitoAnnotations.openMocks(this);

    // Set up some test-data for the test database:
    MasterUser masterUser1 = new MasterUser(0, "TestMasterUser1", passwordEncoder.encode("ads91234AVA'S7_:&)/(=9"));
    MasterUser masterUser2 = new MasterUser(0, "TestMasterUser2", passwordEncoder.encode("ads91234AVA'S7_:&)/(=9"));
    MasterUser masterUser3 = new MasterUser(0, "TestMasterUser3", passwordEncoder.encode("ads91234AVA'S7_:&)/(=9"));
    dbMasterUserRepository.save(masterUser1);
    dbMasterUserRepository.save(masterUser2);
    dbMasterUserRepository.save(masterUser3);
  }

  @AfterEach
  public void tearDown() {
    // Empty
  }


  // ZERO / Null Tests below:
  @Test public void whenCreateMasterUser_IsGivenNullMasterUser_ThrowsDataIntegrityViolationException() {
    assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.createMasterUser(null));
  }

  @Test public void whenCreateMasterUser_IsGivenMasterUserWithZeroId_DoesNotThrowAnyException() {
    assertDoesNotThrow(() -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, "TestMasterUser", "ads91234AVA'S7_:&)/(=9")));
  }

  @Test public void whenCreateMasterUser_IsGivenMasterUserWithNullUserName_ThrowsDataIntegrityViolationException() {
    assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, null, "ads91234AVA'S7_:&)/(=9")));
  }


  @Test public void whenCreateMasterUser_IsGivenMasterUserWithBlankUserName_ThrowsDataIntegrityViolationException() {
    assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, "", "ads91234AVA'S7_:&)/(=9")));
  }


  @Test public void whenCreateMasterUser_IsGivenMasterUserWithNullPassword_ThrowsDataIntegrityViolationException() {
    assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, "TestMasterUser", null)));
  }


  @Test public void whenCreateMasterUser_IsGivenMasterUserWithBlankPassword_ThrowsDataIntegrityViolationException() {
    assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, "TestMasterUser", "")));
  }


  // One tests below:
  @Test public void whenCreateMasterUser_IsGivenValidMasterUser_ReturnsSavedMasterUser() {
    // Arrange:
    MasterUser masterUser = new MasterUser(0, "TestMasterUser", "ads91234AVA'S7_:&)/(=9");

    // Act
    masterUser = dbMasterUserRepositoryService.createMasterUser(masterUser);

    // Assert: Expected id of created MasterUser is 4 (Check Setup and count number of added MasterUsers)
    assertEquals(4, masterUser.getId());
  }

  @Test public void whenCreateMasterUser_IsGivenMasterUserWithNonZeroId_ThrowsDataIntegrityViolationException() {
    assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(1, "TestMasterUser", "ads91234AVA'S7_:&)/(=9")));
  }

  @Test public void whenCreateMasterUser_IsGivenMasterUserWithNonNullNonBlankUserName_DoesNotThrowAnyException() {
    assertDoesNotThrow(() -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, "TestMasterUser", "ads91234AVA'S7_:&)/(=9")));
  }

  @Test public void whenCreateMasterUser_IsGivenMasterUserWithNonNullNonBlankPasswordWithLengthAbove12Chars_DoesNotThrowAnyException() {
    assertDoesNotThrow(() -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, "TestMasterUser", "ads91234AVA'S7_:&)/(=9")));
  }


  // Many tests below:
  @Test public void whenCreateMasterUser_IsGivenAnAlreadyExistingMasterUser_ThrowsDuplicateDbEntryException() {
    assertThrows(DuplicateDbEntryException.class, () -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, "TestMasterUser1", "ads91234AVA'S7_:&)/(=9")));
  }


  // Boundary tests below:
  @Test public void whenCreateMasterUser_IsGivenMasterUserWithPasswordLengthEqualTo11Chars_ThrowsDataIntegrityViolationException() {
    assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, "TestMasterUser", "12345678901")));
  }

  @Test public void whenCreateMasterUser_IsGivenMasterUserWithPasswordLengthEqualTo12Chars_DoesNotThrowAnyException() {
    assertDoesNotThrow(() -> dbMasterUserRepositoryService.createMasterUser(new MasterUser(0, "TestMasterUser", "123456789012")));
  }


  // Interface tests below: Verify interaction with JPA repository Interface
  // Non defined. If above tests work, then interaction with interface is working as intended.

  // Exception tests:
  // None defined. If above tests work, then exceptions have also been checked.

  // Simple Scenario:
  // Already tested as part of the 'One' tests.

}
