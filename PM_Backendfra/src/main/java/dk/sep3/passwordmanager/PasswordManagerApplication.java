package dk.sep3.passwordmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class PasswordManagerApplication
{
  public static void main(String[] args)
  {
    // Run Load Balancer:
    SpringApplication.run(PasswordManagerApplication.class, args);
  }

}
