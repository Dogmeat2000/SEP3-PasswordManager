package dk.sep3.dbdiscoveryservice;

import dk.sep3.dbdiscoveryservice.application.DbDiscoveryServiceApplication;
import dk.sep3.dbserver.encryption.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest (classes = DbDiscoveryServiceApplication.class)
class DbDiscoveryServiceApplicationTests
{

  @Test void contextLoads() {
  }

}
