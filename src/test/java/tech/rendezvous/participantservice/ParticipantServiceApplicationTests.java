package tech.rendezvous.participantservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import tech.rendezvous.participantservice.domain.Participant;
import tech.rendezvous.participantservice.domain.ParticipantModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParticipantServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPostRequestThenParticipantCreated() {
        var expectedParticipantModel = new ParticipantModel(List.of("anders.and@andeby.dk"), "Anders And");

        webTestClient
                .post()
                .uri("/participants")
                .bodyValue(expectedParticipantModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Participant.class).value(actualParticipant -> {
                    assertThat(actualParticipant).isNotNull();
                    assertThat(actualParticipant.name()).isEqualTo(expectedParticipantModel.name());
                    assertThat(actualParticipant.usernames()).isEqualTo(expectedParticipantModel.usernames());
                });
    }

    @Test
    void contextLoads() {
    }

}