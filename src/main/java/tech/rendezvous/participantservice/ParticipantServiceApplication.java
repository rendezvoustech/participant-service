package tech.rendezvous.participantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ParticipantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParticipantServiceApplication.class, args);
    }

}
