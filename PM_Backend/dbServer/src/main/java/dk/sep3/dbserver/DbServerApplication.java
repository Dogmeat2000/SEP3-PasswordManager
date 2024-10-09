package dk.sep3.dbserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "dk.sep3.dbServer.db_entities")
public class DbServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbServerApplication.class, args);
    }

}
