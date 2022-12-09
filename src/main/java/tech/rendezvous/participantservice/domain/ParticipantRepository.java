package tech.rendezvous.participantservice.domain;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    Optional<Participant> findByUsername(String username);



}
