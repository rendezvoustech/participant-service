package tech.rendezvous.participantservice.domain;

import java.util.Optional;

public interface ParticipantRepository {
    Iterable<Participant> findAll();
    Optional<Participant> findOne(String id);

    Participant add(ParticipantModel model);

    Optional<Participant> findOneByUsername(String username);

    void remove(String id);

    Participant save(Participant update);
}
