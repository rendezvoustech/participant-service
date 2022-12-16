package tech.rendezvous.participantservice.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;
import tech.rendezvous.participantservice.config.DataConfig;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(
        replace=AutoConfigureTestDatabase.Replace.NONE
)
@ActiveProfiles("integration")
public class ParticipantRepositoryJdbcTests {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Test
    void findAllParticipants() {
        var participant1 = Participant.of("user name A", "name A");
        var participant2 = Participant.of("user name B", "name B");

        jdbcAggregateTemplate.insert(participant1);
        jdbcAggregateTemplate.insert(participant2);

        Iterable<Participant> actualParticipants = participantRepository.findAll();

        assertThat(StreamSupport.stream(actualParticipants.spliterator(), true)
                .filter(participant -> participant.username().equals(participant1.username()) || participant.username().equals(participant2.username()))
                .collect(Collectors.toList())).hasSize(2);
    }
    @Test
    void findParticipantByUsernameWhenExisting() {
        var username = "anders@and.dk";
        var participant = Participant.of(username, "Anders And");
        jdbcAggregateTemplate.insert(participant);

        Optional<Participant> actualParticipant = participantRepository.findByUsername(username);

        assertThat(actualParticipant).isNotEmpty();
        assertThat(actualParticipant.get().username()).isEqualTo(username);
    }

    @Test
    void findParticipantByUsernameWhenNotExisting() {
        var username = "anders@and.dk";
        var participant = Participant.of(username, "Anders And");
        jdbcAggregateTemplate.insert(participant);

        Optional<Participant> actualParticipant = participantRepository.findByUsername(username+"aa");

        assertThat(actualParticipant).isEmpty();
    }

    @Test
    void existsByUsernameWhenExisting() {
        var username = "anders@and.dk";
        var participant = Participant.of(username, "Anders And");
        jdbcAggregateTemplate.insert(participant);

        boolean existing = participantRepository.existsByUsername(username);

        assertThat(existing).isTrue();
    }

    @Test
    void existsByUsernameWhenNotExisting() {
        var username = "anders@and.dk";
        var participant = Participant.of(username, "Anders And");
        jdbcAggregateTemplate.insert(participant);

        boolean existing = participantRepository.existsByUsername(username + "aa");

        assertThat(existing).isFalse();
    }
}
