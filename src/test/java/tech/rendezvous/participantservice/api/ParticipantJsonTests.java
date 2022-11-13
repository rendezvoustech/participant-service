package tech.rendezvous.participantservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import tech.rendezvous.participantservice.domain.Participant;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ParticipantJsonTests {
    @Autowired
    private JacksonTester<Participant> json;

    @Test
    void testSerialize() throws Exception {
        var expected = new Participant("ID455565FG", Arrays.asList("anders.and@andeby.com", "aa@andeby.com"), "Anders And");
        var jsonContent = json.write(expected);

        assertThat(jsonContent).extractingJsonPathStringValue("id").isEqualTo(expected.id());
        assertThat(jsonContent).extractingJsonPathArrayValue("usernames").isEqualTo(expected.usernames());
        assertThat(jsonContent).extractingJsonPathStringValue("name").isEqualTo(expected.name());
    }

    @Test
    void testDeserialize() throws Exception {
        var content = """
                {
                    "id": "ID675FGFS",
                    "usernames": [
                        "joakim@vonand.dk"
                    ],
                    "name": "Joakim von And"
                }
                """;
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new Participant("ID675FGFS", List.of("joakim@vonand.dk"), "Joakim von And"));
    }
}
