package tech.rendezvous.participantservice.persistence;

import org.springframework.stereotype.Repository;
import tech.rendezvous.participantservice.domain.Participant;
import tech.rendezvous.participantservice.domain.ParticipantModel;
import tech.rendezvous.participantservice.domain.ParticipantNotFoundException;
import tech.rendezvous.participantservice.domain.ParticipantRepository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
public class InMemoryParticipantRepository implements ParticipantRepository {
    private final List<Participant> participants = new ArrayList<>();

    @Override
    public Iterable<Participant> findAll() {
        return participants;
    }

    @Override
    public Optional<Participant> findOne(String id) throws ParticipantNotFoundException {
        return participants.stream().filter(participant -> participant.id().equals(id)).findFirst();
    }

    @Override
    public Participant add(ParticipantModel model) {
        Participant participant = new Participant(UUID.randomUUID().toString().replaceAll("-", ""), model.usernames(), model.name());
        return save(participant);
    }

    @Override
    public Optional<Participant> findOneByUsername(String username) {
        return participants.stream().filter(participant -> {
            for (String u: participant.usernames()
                 ) {
                if (u.equals(username))
                    return true;
            }
            return false;
        }).findFirst();
    }

    @Override
    public void remove(String id) {
        Optional<Participant> participant = findOne(id);
        participant.ifPresent(participants::remove);
    }

    @Override
    public Participant save(Participant update) {
        participants.add(update);
        return update;
    }

    @PostConstruct
    private void init() {
        participants.add(new Participant("AA€%34",
                Arrays.asList("mail@host1.com", "anders@host2.com"),
                "Anders And"));
        participants.add(new Participant("BB65FG",
                List.of("mail2@domain1.net"),
                "Bjarne Bed"));
        participants.add(new Participant("CCRF45",
                List.of("email@cc.org"),
                "Carlo Citron"));
    }
}
