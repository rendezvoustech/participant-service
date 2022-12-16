package tech.rendezvous.participantservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import tech.rendezvous.participantservice.config.MethodSecurityConfig;
import tech.rendezvous.participantservice.config.SecurityConfig;
import tech.rendezvous.participantservice.domain.*;
import tech.rendezvous.participantservice.security.AuthenticationService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tech.rendezvous.participantservice.security.AuthenticationService.ROLE_ADMINISTRATOR;
import static tech.rendezvous.participantservice.security.AuthenticationService.ROLE_USER;

@WebMvcTest(ParticipantRestController.class)
@Import({SecurityConfig.class, MethodSecurityConfig.class})
public class ParticipantRestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ParticipantService participantService;
    @MockBean
    private ParticipantRepository participantRepository;

    @MockBean AuthenticationService authenticationService;
    @Test
    void whenGetParticipantsAndAuthenticatedAdministratorThenShouldReturnAllParticipants() throws Exception {
        var expectedParticipantA = new Participant(1L, "user name A", "name A", Instant.now(), Instant.now(), 1);
        var expectedParticipantB = new Participant(2L, "user name B", "name B", Instant.now(), Instant.now(), 1);
        given(participantService.viewList()).willReturn(List.of(expectedParticipantA, expectedParticipantB));
        given(authenticationService.isAdministrator(any())).willReturn(true);
        mockMvc
                .perform(get("/participants/")
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedParticipantA, expectedParticipantB))))
        ;
    }

    @Test
    void whenGetParticipantsAndAuthenticatedAdministratorThenShouldReturn200() throws Exception {
        given(authenticationService.isAdministrator(any())).willReturn(true);
        mockMvc
                .perform(get("/participants/")
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))))
                .andExpect(status().isOk())
        ;
    }
    @Test
    void whenGetParticipantsAndAuthenticatedUserThenShouldReturnOnlyTheParticipant() throws Exception {
        var expectedParticipantA = new Participant(1L, "user name A", "name A", Instant.now(), Instant.now(), 1);
        var expectedParticipantB = new Participant(2L, "user name B", "name B", Instant.now(), Instant.now(), 1);
        given(participantService.viewList()).willReturn(List.of(expectedParticipantA, expectedParticipantB));
        given(authenticationService.isAdministrator(any())).willReturn(false);
        given(authenticationService.username(any())).willReturn("user name B");
        mockMvc
                .perform(get("/participants/")
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedParticipantB))))
        ;
    }

    @Test
    void whenGetParticipantsAndAuthenticatedUserThenShouldReturn200() throws Exception {
        given(authenticationService.isAdministrator(any())).willReturn(false);
        given(authenticationService.username(any())).willReturn("user name B");
        mockMvc
                .perform(get("/participants/")
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER))))
                .andExpect(status().isOk())
        ;
    }

    @Test
    void whenGetParticipantsAndNotAuthenticatedThenNoInteractionWIthBusinessService() throws Exception {
        mockMvc
                .perform(get("/participants/"));
        verifyNoInteractions(participantService);
    }
    @Test
    void whenGetParticipantsAndNotAuthenticatedThenShouldReturn401() throws Exception {
        mockMvc
                .perform(get("/participants/"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void whenGetParticipantExistingAndAuthenticatedAdministratorThenShouldReturnTheParticipant() throws Exception {
        var id = 7373731394L;
        var expectedParticipant = new Participant(id, "user name", "name", Instant.now(), Instant.now(), 1);
        given(participantService.viewDetails(id)).willReturn(expectedParticipant);
        mockMvc
                .perform(get("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedParticipant)))
        ;
    }

    @Test
    void whenGetParticipantExistingAndAuthenticatedAdministratorThenShouldReturn200() throws Exception {
        var id = 7373731394L;
        mockMvc
                .perform(get("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))))
                .andExpect(status().isOk())
        ;
    }

    @Test
    void whenGetParticipantNotExistingAndAuthenticatedAdministratorThenShouldReturn404() throws Exception {
        var id = 7373731394L;
        given(participantService.viewDetails(id)).willThrow(new ParticipantNotFoundException(id));
        mockMvc
                .perform(get("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetParticipantExistingAndAuthenticatedUserRequestingOwnThenShouldReturnTheParticipant() throws Exception {
        var id = 7373731394L;
        var user = new Participant(id, "superman@dc.com", "name", Instant.now(), Instant.now(), 1);
        given(participantRepository.findById(id)).willReturn(Optional.of(user));
        given(participantService.viewDetails(id)).willReturn(user);
        mockMvc
                .perform(get("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void whenGetParticipantExistingAndAuthenticatedUserRequestingOwnThenShouldReturn200() throws Exception {
        var id = 7373731394L;
        var user = new Participant(id, "superman@dc.com", "name", Instant.now(), Instant.now(), 1);
        given(participantRepository.findById(id)).willReturn(Optional.of(user));
        mockMvc
                .perform(get("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void whenGetParticipantExistingAndAuthenticatedUserRequestingOtherThenShouldHaveNoInteractionWithBusinessService() throws Exception {
        var id = 7373731394L;
        given(participantRepository.findById(id)).willReturn(Optional.of(new Participant(id, "batman@dc.com", "name", Instant.now(), Instant.now(), 1)));
        mockMvc
                .perform(get("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))));
        verifyNoInteractions(participantService);
    }

    @Test
    void whenGetParticipantExistingAndAuthenticatedUserRequestingOtherThenShouldReturn403() throws Exception {
        var id = 7373731394L;
        given(participantRepository.findById(id)).willReturn(Optional.of(new Participant(id, "batman@dc.com", "name", Instant.now(), Instant.now(), 1)));
        mockMvc
                .perform(get("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenGetParticipantExistingAndNotAuthenticatedThenSHouldHaveNoInteractionWithBusinessService() throws Exception {
        var id = 7373731394L;
        mockMvc
                .perform(get("/participants/" + id));
        verifyNoInteractions(participantService);
    }

    @Test
    void whenGetParticipantExistingAndNotAuthenticatedThenShouldReturn401() throws Exception {
        var id = 7373731394L;
        mockMvc
                .perform(get("/participants/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenDeleteParticipantAndAuthenticatedAdministratorThenShouldRemoveParticipant() throws Exception {
        var id = 7373731394L;
        mockMvc
                .perform(delete("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))));
        verify(participantService).remove(id);
    }

    @Test
    void whenDeleteParticipantAndAuthenticatedAdministratorThenShouldReturn204() throws Exception {
        var id = 7373731394L;
        mockMvc
                .perform(delete("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteParticipantAndAuthenticatedUserRequestingOwnThenShouldRemoveParticipant() throws Exception {
        var id = 7373731394L;
        var user = new Participant(id, "superman@dc.com", "name", Instant.now(), Instant.now(), 1);
        given(participantRepository.findById(id)).willReturn(Optional.of(user));
        mockMvc
                .perform(delete("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))));
        verify(participantService).remove(id);
    }

    @Test
    void whenDeleteParticipantAndAuthenticatedUserRequestingOwnThenShouldReturn204() throws Exception {
        var id = 7373731394L;
        var user = new Participant(id, "superman@dc.com", "name", Instant.now(), Instant.now(), 1);
        given(participantRepository.findById(id)).willReturn(Optional.of(user));
        mockMvc
                .perform(delete("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteParticipantAndAuthenticatedUserRequestingOtherThenShouldRHaveNoInteractionWithBusinessService() throws Exception {
        var id = 7373731394L;
        given(participantRepository.findById(id)).willReturn(Optional.of(new Participant(id, "batman@dc.com", "name", Instant.now(), Instant.now(), 1)));
        mockMvc
                .perform(delete("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))));
        verifyNoInteractions(participantService);
    }

    @Test
    void whenDeleteParticipantAndAuthenticatedUserRequestingOtherThenShouldReturn403() throws Exception {
        var id = 7373731394L;
        given(participantRepository.findById(id)).willReturn(Optional.of(new Participant(id, "batman@dc.com", "name", Instant.now(), Instant.now(), 1)));
        mockMvc
                .perform(delete("/participants/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenDeleteParticipantAndNotAuthenticatedThenShouldHaveNoInteractionWithBusinessService() throws Exception {
        var id = 7373731394L;
        mockMvc
                .perform(delete("/participants/" + id));
        verifyNoInteractions(participantService);
    }

    @Test
    void whenDeleteParticipantAndNotAuthenticatedThenShouldReturn401() throws Exception {
        var id = 7373731394L;
        mockMvc
                .perform(delete("/participants/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenPutParticipantAndAuthenticatedAdministratorThenShouldUpdateParticipant() throws Exception {
        var id = 7373731394L;
        var participantToUpdate = new ParticipantModel("username", "name");
        mockMvc
                .perform(put("/participants/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToUpdate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))));
        verify(participantService).editDetails(id, participantToUpdate);
    }

    @Test
    void whenPutParticipantAndAuthenticatedAdministratorThenShouldReturn200() throws Exception {
        var id = 7373731394L;
        var participantToUpdate = new ParticipantModel("username", "name");
        mockMvc
                .perform(put("/participants/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToUpdate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutParticipantAndAuthenticatedUserRequestingOwnThenUpdateParticipant() throws Exception {
        var id = 7373731394L;
        var user = new Participant(id, "superman@dc.com", "name", Instant.now(), Instant.now(), 1);
        var participantToUpdate = new ParticipantModel("username", "name");
        given(participantRepository.findById(id)).willReturn(Optional.of(user));
        mockMvc
                .perform(put("/participants/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToUpdate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))));
        verify(participantService).editDetails(id, participantToUpdate);
    }

    @Test
    void whenPutParticipantAndAuthenticatedUserRequestingOwnThenShouldReturn200() throws Exception {
        var id = 7373731394L;
        var user = new Participant(id, "superman@dc.com", "name", Instant.now(), Instant.now(), 1);
        var participantToUpdate = new ParticipantModel("username", "name");
        given(participantRepository.findById(id)).willReturn(Optional.of(user));
        mockMvc
                .perform(put("/participants/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToUpdate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutParticipantAndAuthenticatedUserRequestingOtherThenShouldHaveNoInteractionWithBusinessService() throws Exception {
        var id = 7373731394L;
        var participantToUpdate = new ParticipantModel("username", "name");
        given(participantRepository.findById(id)).willReturn(Optional.of(new Participant(id, "batman@dc.com", "name", Instant.now(), Instant.now(), 1)));
        mockMvc
                .perform(put("/participants/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToUpdate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))));
        verifyNoInteractions(participantService);
    }

    @Test
    void whenPutParticipantAndAuthenticatedUserRequestingOtherThenShouldReturn403() throws Exception {
        var id = 7373731394L;
        var participantToUpdate = new ParticipantModel("username", "name");
        given(participantRepository.findById(id)).willReturn(Optional.of(new Participant(id, "batman@dc.com", "name", Instant.now(), Instant.now(), 1)));
        mockMvc
                .perform(put("/participants/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToUpdate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenPutParticipantAndNotAuthenticatedThenShouldHaveNoInteractionWithBusinessService() throws Exception {
        var id = 7373731394L;
        var participantToUpdate = new ParticipantModel("username", "name");
        mockMvc
                .perform(put("/participants/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToUpdate))
                );
        verifyNoInteractions(participantService);
    }

    @Test
    void whenPutParticipantAndNotAuthenticatedThenShouldReturn401() throws Exception {
        var id = 7373731394L;
        var participantToUpdate = new ParticipantModel("username", "name");
        mockMvc
                .perform(put("/participants/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToUpdate))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenPostParticipantAndAuthenticatedAdministratorThenShouldCreateParticipant201() throws Exception {
        var participantToCreate = new ParticipantModel("username", "name");
        mockMvc
                .perform(post("/participants/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))));
        verify(participantService).add(participantToCreate);
    }

    @Test
    void whenPostParticipantAndAuthenticatedAdministratorThenShouldReturn201() throws Exception {
        var participantToCreate = new ParticipantModel("username", "name");
        mockMvc
                .perform(post("/participants/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR))))
                .andExpect(status().isCreated());
    }

    @Test
    void whenPostParticipantAndAuthenticatedUserCreatingSameUsernameThenShouldCreateParticipant() throws Exception {
        var participantToCreate = new ParticipantModel("superman@dc.com", "superman");
        mockMvc
                .perform(post("/participants/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))));
        verify(participantService).add(participantToCreate);
    }

    @Test
    void whenPostParticipantAndAuthenticatedUserCreatingSameUsernameThenShouldReturn201() throws Exception {
        var participantToCreate = new ParticipantModel("superman@dc.com", "superman");
        mockMvc
                .perform(post("/participants/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("superman@dc.com"))))
                .andExpect(status().isCreated());
    }

    @Test
    void whenPostParticipantAndAuthenticatedUserCreatingDifferentUsernameThenShouldHaveNoInteractionWithBusinessService() throws Exception {
        var participantToCreate = new ParticipantModel("superman@dc.com", "superman");
        mockMvc
                .perform(post("/participants/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("batman@dc.com"))));
        verifyNoInteractions(participantService);
    }

    @Test
    void whenPostParticipantAndAuthenticatedUserCreatingDifferentUsernameThenShouldReturn403() throws Exception {
        var participantToCreate = new ParticipantModel("superman@dc.com", "superman");
        mockMvc
                .perform(post("/participants/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_USER)).jwt(j -> j.subject("batman@dc.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenPostParticipantAndNotAuthenticatedThenShouldReturnHaveNoInteractionWithBusinessService() throws Exception {
        var participantToCreate = new ParticipantModel("username", "name");
        mockMvc
                .perform(post("/participants/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantToCreate)));
        verifyNoInteractions(participantService);
    }

    @Test
    void whenPostParticipantAndNotAuthenticatedThenShouldReturn401() throws Exception {
        var participantToCreate = new ParticipantModel("username", "name");
        mockMvc
                .perform(post("/participants/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(participantToCreate)))
                .andExpect(status().isUnauthorized());
    }
}
