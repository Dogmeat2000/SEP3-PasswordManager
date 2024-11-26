package dk.sep3.loadbalancer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/** Main class to start the LoadBalancer application **/
@SpringBootApplication
@ComponentScan({"dk.sep3.loadbalancer", "dk.sep3.webapi"})
public class LoadBalancerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoadBalancerApplication.class, args);
    }
}
