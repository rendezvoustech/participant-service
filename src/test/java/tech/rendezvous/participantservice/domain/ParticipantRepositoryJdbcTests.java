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
        var participant = Participant.of(List.of(username), "Anders And");
        jdbcAggregateTemplate.insert(participant);

        List<Participant> actualParticipants = participantRepository.findByUsername(username);

        assertThat(actualParticipants).isNotEmpty();
        assertThat(actualParticipants.size()).isEqualTo(1);
        assertThat(actualParticipants.get(0).usernames()).contains(username);
    }

    @Test
    void findParticipantByUsernameAmongSeveral() {
        var username = "anders@and.dk";
        var participant = Participant.of(List.of("aa@hotmail.com", username, "a.and@andeby.dk"), "Anders And");
        jdbcAggregateTemplate.insert(participant);

        List<Participant> actualParticipants = participantRepository.findByUsername(username);

        assertThat(actualParticipants).isNotEmpty();
        assertThat(actualParticipants.size()).isEqualTo(1);
        assertThat(actualParticipants.get(0).usernames()).contains(username);
    }

    @Test
    void findParticipantByUsernameNotFound() {
        var username = "anders@and.dk";
        var participant = Participant.of(List.of(username), "Anders And");
        jdbcAggregateTemplate.insert(participant);

        List<Participant> actualParticipants = participantRepository.findByUsername(username+"aa");

        assertThat(actualParticipants).isEmpty();
    }
}
