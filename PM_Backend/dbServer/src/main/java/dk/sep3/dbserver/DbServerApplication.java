package dk.sep3.dbserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**<p>Primary Spring Boot Application class, responsible for launching each individual Database Server.</p>
 * <p>Note: This application is NOT intended to be run manually. Please instead let the Discovery Service handle the launch of each Database Server.</p>
 * <p>However, should the Discovery Service fail it is entirely safe to manually force a Database Server to launch by running this application/class.</p>*/
@SpringBootApplication
@EntityScan(basePackages = {"dk.sep3.dbserver.model.passwordManager.db_entities", "dk.sep3.dbserver.model.discoveryService.db_entities"})
public class DbServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbServerApplication.class, args);
    }

}
