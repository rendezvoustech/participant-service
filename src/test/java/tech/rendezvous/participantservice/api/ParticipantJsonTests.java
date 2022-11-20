package tech.rendezvous.participantservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import tech.rendezvous.participantservice.domain.Participant;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ParticipantJsonTests {
    @Autowired
    private JacksonTester<Participant> json;

    @Test
    void testSerialize() throws Exception {
        var now = Instant.now();
        var expected = new Participant(44434434L, Arrays.asList("anders.and@andeby.com", "aa@andeby.com"), "Anders And", now, now, 21);
        var jsonContent = json.write(expected);

        assertThat(jsonContent).extractingJsonPathNumberValue("@.id").isEqualTo(expected.id().intValue());
        assertThat(jsonContent).extractingJsonPathArrayValue("@.usernames").isEqualTo(expected.usernames());
        assertThat(jsonContent).extractingJsonPathStringValue("@.name").isEqualTo(expected.name());
        assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate")
                .isEqualTo(expected.createdDate().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate")
                .isEqualTo(expected.lastModifiedDate().toString());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
                .isEqualTo(expected.version());
    }

    @Test
    void testDeserialize() throws Exception {
        var instant = Instant.parse("2021-09-07T22:50:37.135029Z");
        var content = """
                {
                    "id": 394,
                    "usernames": [
                        "joakim@vonand.dk"
                    ],
                    "name": "Joakim von And",
                    "createdDate": "2021-09-07T22:50:37.135029Z",
                    "lastModifiedDate": "2021-09-07T22:50:37.135029Z",
                    "version": 21
                }
                """;
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new Participant(394L, List.of("joakim@vonand.dk"), "Joakim von And", instant, instant, 21));
    }
}
