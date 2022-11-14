package tech.rendezvous.participantservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.rendezvous.participantservice.config.RendezvousProperties;

@RestController
public class HomeController {
    private final RendezvousProperties rendezvousProperties;

    public HomeController(RendezvousProperties rendezvousProperties) {
        this.rendezvousProperties = rendezvousProperties;
    }

    @GetMapping("/")
    public String getGreeting() {
        return rendezvousProperties.getGreeting();
    }
}
