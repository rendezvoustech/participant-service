package tech.rendezvous.participantservice.domain;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    @Query("SELECT * FROM participant p WHERE :username = ANY(p.usernames)")
    List<Participant> findByUsername(String username);



}
