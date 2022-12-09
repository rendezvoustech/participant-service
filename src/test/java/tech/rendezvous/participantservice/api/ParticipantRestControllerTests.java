package tech.rendezvous.participantservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import tech.rendezvous.participantservice.domain.ParticipantNotFoundException;
import tech.rendezvous.participantservice.domain.ParticipantService;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParticipantRestController.class)
public class ParticipantRestControllerTests {
    private static final String ROLE_EMPLOYEE = "ROLE_employee";
    private static final String ROLE_CUSTOMER = "ROLE_customer";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantService participantService;

    @Test
    public void whenGetParticipantNotExistingThenShouldReturn404() throws Exception {
        Long id =10L;
        given(participantService.viewDetails(id))
                .willThrow(ParticipantNotFoundException.class);
        mockMvc.perform(get("/participants/" + id).with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER))))
                .andExpect(status().isNotFound());
    }
}
