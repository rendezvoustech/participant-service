package tech.rendezvous.participantservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.rendezvous.participantservice.domain.Participant;
import tech.rendezvous.participantservice.domain.ParticipantModel;
import tech.rendezvous.participantservice.domain.ParticipantService;
import tech.rendezvous.participantservice.security.AuthenticationService;

import javax.validation.Valid;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("participants")
public class ParticipantRestController {
    private static final Logger log = LoggerFactory.getLogger(ParticipantRestController.class);
    private final ParticipantService participantService;

    private final AuthenticationService authenticationService;
    public ParticipantRestController(ParticipantService participantService, AuthenticationService authenticationService) {
        this.participantService = participantService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public Iterable<Participant> getParticipants(Authentication authentication) {
        log.info("Fetching the list of participants");
        if (authenticationService.isAdministrator(authentication))
            return participantService.viewList();
        return StreamSupport.stream(participantService.viewList().spliterator(), false)
                .filter(p -> p.username().equals(authenticationService.username(authentication)))
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ROLE_administrator') or isParticipant(#id)")
    public Participant getParticipant(@PathVariable Long id) {
        log.info("Fetching the participant with id {} from the database", id);
        return participantService.viewDetails(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_administrator') or hasUsername(#model)")
    public Participant post(@RequestBody @Valid ParticipantModel model) {
        log.info("Adding a new participant to the database with name {}", model.name());
        return participantService.add(model);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_administrator') or isParticipant(#id)")
    public void delete(@PathVariable Long id) {
        log.info("Deleting participant with id {}", id);
        participantService.remove(id);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_administrator') or isParticipant(#id)")
    public Participant put(@PathVariable Long id, @RequestBody @Valid ParticipantModel model) {
        log.info("Updating participant with id {}", id);
        return participantService.editDetails(id, model);
    }
}

