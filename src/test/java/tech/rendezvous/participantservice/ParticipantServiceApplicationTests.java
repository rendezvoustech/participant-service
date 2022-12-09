package tech.rendezvous.participantservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.rendezvous.participantservice.domain.Participant;
import tech.rendezvous.participantservice.domain.ParticipantModel;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
class ParticipantServiceApplicationTests {

    // Customer
    private static KeycloakToken bjornTokens;
    // Customer and employee
    private static KeycloakToken isabelleTokens;

    @Autowired
    private WebTestClient webTestClient;

    @Container
    private static final KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:19.0")
            .withRealmImportFile("test-realm-config.json");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "realms/RendezvousTech");
    }

    @BeforeAll
    static void generateAccessTokens() {
        WebClient webClient = WebClient.builder()
                .baseUrl(keycloakContainer.getAuthServerUrl() + "realms/RendezvousTech/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        isabelleTokens = authenticateWith("isabelle", "password", webClient);
        bjornTokens = authenticateWith("bjorn", "password", webClient);
    }

    @Test
    void whenGetRequestWithIdThenBookReturned() {
        var participantToCreate = new ParticipantModel("User Name", "Name");
        Participant expectedParticipant = webTestClient
                .post()
                .uri("/participants")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(participantToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Participant.class).value(participant -> assertThat(participant).isNotNull())
                .returnResult().getResponseBody();

        webTestClient
                .get()
                .uri("/participants/" + expectedParticipant.id())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Participant.class).value(actualParticipant -> {
                    assertThat(actualParticipant).isNotNull();
                    assertThat(actualParticipant.id()).isEqualTo(expectedParticipant.id());
                });
    }
    @Test
    void whenPostRequestThenParticipantCreated() {
        var expectedParticipantModel = new ParticipantModel("anders.and@andeby.dk", "Anders And");

        webTestClient
                .post()
                .uri("/participants")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(expectedParticipantModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Participant.class).value(actualParticipant -> {
                    assertThat(actualParticipant).isNotNull();
                    assertThat(actualParticipant.name()).isEqualTo(expectedParticipantModel.name());
                    assertThat(actualParticipant.username()).isEqualTo(expectedParticipantModel.username());
                });
    }

    @Test
    void whenPostRequestUnauthenticatedThen401() {
        var expectedParticipant = Participant.of("username", "name");

        webTestClient
                .post()
                .uri("/participants")
                .bodyValue(expectedParticipant)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenPutRequestThenParticipantUpdated() {
        var participantToCreate = new ParticipantModel("User Name", "Name");
        Participant createdParticipant = webTestClient
                .post()
                .uri("/participants")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(participantToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Participant.class).value(participant -> assertThat(participant).isNotNull())
                .returnResult().getResponseBody();

        var participantToUpdate = new ParticipantModel("User Name II", "Name II");

        webTestClient
                .put()
                .uri("/participants/" + createdParticipant.id())
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(participantToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Participant.class).value(actualParticipant -> {
                    assertThat(actualParticipant).isNotNull();
                    assertThat(actualParticipant.username()).isEqualTo(participantToUpdate.username());
                    assertThat(actualParticipant.name()).isEqualTo(participantToUpdate.name());
                });
    }

    @Test
    void whenDeleteRequestThenParticipantDeleted() {
        var participantToCreate = new ParticipantModel("User Name A", "Name A");
        Participant createdParticipant = webTestClient
                .post()
                .uri("/participants")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(participantToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Participant.class).value(participant -> assertThat(participant).isNotNull())
                .returnResult().getResponseBody();

        webTestClient
                .delete()
                .uri("/participants/" + createdParticipant.id())
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .exchange()
                .expectStatus().isNoContent();

        webTestClient
                .get()
                .uri("/participants/" + createdParticipant.id())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).value(errorMessage ->
                        assertThat(errorMessage).isEqualTo("A participant with id " + createdParticipant.id() + " was not found.")
                );
    }
    private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "rendezvous-test")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }

    private record KeycloakToken(String accessToken) {

        @JsonCreator
        private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }

    }
}
