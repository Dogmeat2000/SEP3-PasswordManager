package dk.sep3.passwordmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "dk.sep3.passwordmanager.dbServer.db_entities")
public class PasswordManagerApplication
{
  public static void main(String[] args)
  {
    // Run Load Balancer:
    SpringApplication.run(PasswordManagerApplication.class, args);
  }

}
