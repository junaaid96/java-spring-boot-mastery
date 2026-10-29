package dev.junaaid96.jobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan of this package.
@SpringBootApplication
// Finds @ConfigurationProperties records (type-safe config).
@ConfigurationPropertiesScan
public class JobBoardApplication {

    // Entry point: starts the embedded server and the application context.
    public static void main(String[] args) {
        SpringApplication.run(JobBoardApplication.class, args);
    }
}
