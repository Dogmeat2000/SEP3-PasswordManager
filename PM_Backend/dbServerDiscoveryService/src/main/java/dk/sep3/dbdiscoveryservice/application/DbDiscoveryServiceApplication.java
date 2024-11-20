package dk.sep3.dbdiscoveryservice.application;

import dk.sep3.dbserver.encryption.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {
    "dk.sep3.dbserver.repositories.config.discoveryServiceDb",
    "dk.sep3.dbserver.repositories.discoveryServiceDb",
    "dk.sep3.dbserver.service.discoveryService",
    "dk.sep3.dbdiscoveryservice.*"
})
@EntityScan(basePackages = {"dk.sep3.dbserver.model.discoveryService.db_entities"})
public class DbDiscoveryServiceApplication
{
  public static void main(String[] args) {
    SpringApplication.run(DbDiscoveryServiceApplication.class, args);
  }
}
