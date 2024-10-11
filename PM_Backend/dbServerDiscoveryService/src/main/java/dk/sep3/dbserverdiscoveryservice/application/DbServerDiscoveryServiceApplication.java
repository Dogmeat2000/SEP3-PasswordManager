package dk.sep3.dbserverdiscoveryservice.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {
    "dk.sep3.dbserver.repositories.config.discoveryServiceDb",
    "dk.sep3.dbserver.repositories.discoveryServiceDb",
    "dk.sep3.dbserver.service.discoveryService",
    "dk.sep3.dbserverdiscoveryservice.*"
})
@EntityScan(basePackages = {"dk.sep3.dbserver.model.discoveryService.db_entities"})
public class DbServerDiscoveryServiceApplication
{
  public static void main(String[] args) {
    SpringApplication.run(DbServerDiscoveryServiceApplication.class, args);
  }
}
