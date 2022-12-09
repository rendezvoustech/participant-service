package tech.rendezvous.participantservice.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;
import tech.rendezvous.participantservice.config.DataConfig;

import java.util.List;
import java.util.Optional;

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
    void findParticipantByUsername() {
        var username = "anders@and.dk";
        var participant = Participant.of(username, "Anders And");
        jdbcAggregateTemplate.insert(participant);

        Optional<Participant> actualParticipant = participantRepository.findByUsername(username);

        assertThat(actualParticipant).isNotEmpty();
        assertThat(actualParticipant.get().username()).isEqualTo(username);
    }

    @Test
    void findParticipantByUsernameNotFound() {
        var username = "anders@and.dk";
        var participant = Participant.of(username, "Anders And");
        jdbcAggregateTemplate.insert(participant);

        Optional<Participant> actualParticipant = participantRepository.findByUsername(username+"aa");

        assertThat(actualParticipant).isEmpty();
    }
}
