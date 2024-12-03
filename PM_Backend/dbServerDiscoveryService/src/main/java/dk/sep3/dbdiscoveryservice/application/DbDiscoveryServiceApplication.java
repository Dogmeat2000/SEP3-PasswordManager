package dk.sep3.dbdiscoveryservice.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**<p>Primary Spring Boot Application class, responsible for launching the Discovery Service -> and launching the individual Database Servers.</p>
 * <p>Note: This application should always be run, when launching the full application stack.</p>*/
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
