package tech.rendezvous.participantservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.rendezvous.participantservice.domain.ParticipantNotFoundException;
import tech.rendezvous.participantservice.domain.ParticipantService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParticipantRestController.class)
public class ParticipantRestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantService participantService;

    @Test
    public void whenGetParticipantNotExistingThenShouldReturn404() throws Exception {
        String id ="ID";
        given(participantService.viewDetails(id))
                .willThrow(ParticipantNotFoundException.class);
        mockMvc.perform(get("/participants/" + id))
                .andExpect(status().isNotFound());
    }
}
