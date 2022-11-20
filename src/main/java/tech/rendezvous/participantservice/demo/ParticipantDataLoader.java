package tech.rendezvous.participantservice.demo;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tech.rendezvous.participantservice.domain.Participant;
import tech.rendezvous.participantservice.domain.ParticipantRepository;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("testdata")
public class ParticipantDataLoader {
    private final ParticipantRepository participantRepository;

    public ParticipantDataLoader(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadParticipantTestData() {
        participantRepository.deleteAll();
       var p1 =
                Participant.of(
                        Arrays.asList("mail@host1.com", "anders@host2.com"),
                        "Anders And");
        var p2 =
                Participant.of(
                        List.of("mail2@domain1.net"),
                        "Bjarne Bed");
        var p3 =
                Participant.of(
                        List.of("email@cc.org"),
                        "Carlo Citron");
        participantRepository.saveAll(List.of(p1, p2, p3));
    }
}
