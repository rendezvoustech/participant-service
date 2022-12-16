package tech.rendezvous.participantservice.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ParticipantRepository extends CrudRepository<Participant, Long> {
    Optional<Participant> findByUsername(String username);
    boolean existsByUsername(String username);
}
