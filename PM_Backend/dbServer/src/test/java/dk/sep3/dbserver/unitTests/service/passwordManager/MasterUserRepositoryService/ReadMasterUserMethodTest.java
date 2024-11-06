package dk.sep3.dbserver.unitTests.service.passwordManager.MasterUserRepositoryService;

import dk.sep3.dbserver.DbServerApplication;
import dk.sep3.dbserver.integrationTests.TestDataSourceConfig;
import dk.sep3.dbserver.model.passwordManager.db_entities.MasterUser;
import dk.sep3.dbserver.repositories.discoveryServiceDb.DiscoveryRepository;
import dk.sep3.dbserver.repositories.passwordManagerDb.MasterUserRepository;
import dk.sep3.dbserver.service.exceptions.NotFoundInDBException;
import dk.sep3.dbserver.service.passwordManager.MasterUserRepositoryServiceImpl;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
  public class ReadMasterUserMethodTest
  {
    @MockBean private DiscoveryRepository dbDiscoveryRepository; // Required field. Must not be removed despite no apparent usage!

    @Autowired private MasterUserRepository dbMasterUserRepository;

    @Autowired private MasterUserRepositoryServiceImpl dbMasterUserRepositoryService;

    @BeforeEach public void setUp() {
      // Initialize all the @Mock and @InjectMock fields, allowing Spring Boot time to perform its Dependency Injection.
      MockitoAnnotations.openMocks(this);

      // Set up some test-data for the test database:
      MasterUser masterUser1 = new MasterUser(0, "TestMasterUser1", "ads91234AVA'S7_:&)/(=9");
      MasterUser masterUser2 = new MasterUser(0, "TestMasterUser2", "ads91234AVA'S7_:&)/(=9");
      MasterUser masterUser3 = new MasterUser(0, "TestMasterUser3", "ads91234AVA'S7_:&)/(=9");
      dbMasterUserRepository.save(masterUser1);
      dbMasterUserRepository.save(masterUser2);
      dbMasterUserRepository.save(masterUser3);
    }

    @AfterEach public void tearDown() {
      // Empty
    }

    // ZERO / Null Tests below:
    @Test public void whenReadMasterUser_IsGivenNullMasterUsername_ThrowsDataIntegrityViolationException() {
      assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.readMasterUser(null, "ads91234AVA'S7_:&)/(=9"));
    }

    @Test public void whenCreateMasterUser_IsGivenBlankMasterUsername_ThrowsDataIntegrityViolationException() {
      assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.readMasterUser("", "ads91234AVA'S7_:&)/(=9"));
    }

    @Test public void whenReadMasterUser_IsGivenNullEncryptedPassword_ThrowsDataIntegrityViolationException() {
      assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.readMasterUser("TestMasterUser3", null));
    }

    @Test public void whenReadMasterUser_IsGivenBlankEncryptedPassword_ThrowsDataIntegrityViolationException() {
      assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.readMasterUser("TestMasterUser3", ""));
    }


    // One tests below:
    @Test public void whenReadMasterUser_IsGivenValidMasterUsernameAndPassword_ReturnsReadMasterUser() {
      // Arrange & Act
      MasterUser masterUser = dbMasterUserRepositoryService.readMasterUser("TestMasterUser1","ads91234AVA'S7_:&)/(=9");

      // Assert: Expected id of created MasterUser is 4 (Check Setup and count number of added MasterUsers)
      assertEquals("TestMasterUser1", masterUser.getMasterUsername());
      assertEquals("ads91234AVA'S7_:&)/(=9", masterUser.getEncryptedPassword());
      assertEquals(1, masterUser.getId());
    }

    @Test public void whenReadMasterUser_IsGivenValidMasterUsernameButInvalidPassword_ThrowsNotFoundInDBException() {
      assertThrows(NotFoundInDBException.class, () -> dbMasterUserRepositoryService.readMasterUser("TestMasterUser1", "invalidPassword1234"));
    }

    @Test public void whenReadMasterUser_IsGivenInvalidMasterUsernameButValidPassword_ThrowsNotFoundInDBException() {
      assertThrows(NotFoundInDBException.class, () -> dbMasterUserRepositoryService.readMasterUser("%&/(&invalidUsername123456789_(&¤(/83762¤#?/(=)(", "ads91234AVA'S7_:&)/(=9"));
    }

    @Test public void whenReadMasterUser_IsGivenEncryptedPasswordWithLengthAbove12Chars_DoesNotThrowAnyException() {
      assertDoesNotThrow(() -> dbMasterUserRepositoryService.readMasterUser("TestMasterUser1","ads91234AVA'S7_:&)/(=9"));
    }


    // Many tests below:
    // None defined.


    // Boundary tests below:
    @Test public void whenReadMasterUser_IsGivenValidMasterUsernameAndInvalidEncryptedPasswordWithEqualTo11Chars_ThrowsDataIntegrityViolationException() {
      assertThrows(DataIntegrityViolationException.class, () -> dbMasterUserRepositoryService.readMasterUser("TestMasterUser1", "12345678901"));
    }

    @Test public void whenReadMasterUser_IsGivenValidMasterUsernameAndInvalidEncryptedPasswordWithEqualTo12Chars_ThrowsNotFoundInDBException() {
      assertThrows(NotFoundInDBException.class, () -> dbMasterUserRepositoryService.readMasterUser("TestMasterUser1", "123456789012"));
    }


    // Interface tests below: Verify interaction with JPA repository Interface
    // None defined. If above tests work, then interaction with interface is working as intended.

    // Exception tests:
    @Test public void whenReadMasterUser_IsGivenMasterUsernameAndPasswordThatDoesNotExistInDb_ThrowsNotFoundInDBException() {
      assertThrows(NotFoundInDBException.class, () -> dbMasterUserRepositoryService.readMasterUser("%&/(&invalidUsername123456789_(&¤(/83762¤#?/(=)(", "invalidPassword1234"));
    }

    @Test public void whenReadMasterUser_IsGivenMasterUsernameThatExistsInDbAndPasswordThatDoesNotExistInDb_ThrowsNotFoundInDBException() {
      assertThrows(NotFoundInDBException.class, () -> dbMasterUserRepositoryService.readMasterUser("TestMasterUser1", "invalidPassword1234"));
    }

    @Test public void whenReadMasterUser_IsGivenMasterUsernameThatDoesNotExistInDbAndPasswordThatDoesExistInDb_ThrowsNotFoundInDBException() {
      assertThrows(NotFoundInDBException.class, () -> dbMasterUserRepositoryService.readMasterUser("%&/(&invalidUsername123456789_(&¤(/83762¤#?/(=)(", "ads91234AVA'S7_:&)/(=9"));
    }

    // Simple Scenario:
    // Already tested as part of the 'One' tests.

  }