package tech.rendezvous.participantservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.rendezvous.participantservice.domain.Participant;
import tech.rendezvous.participantservice.domain.ParticipantModel;
import tech.rendezvous.participantservice.domain.ParticipantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;

@RestController
@RequestMapping("participants")
public class ParticipantRestController {
    private static final Logger log = LoggerFactory.getLogger(ParticipantRestController.class);
    private final ParticipantService participantService;

    public ParticipantRestController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping
    public Iterable<Participant> getParticipants() {
        log.info("Fetching the list of participants");
        return participantService.viewList();
    }

    @GetMapping("{id}")
    public Participant getParticipant(@PathVariable Long id) {
        log.info("Fetching the participant with id {} from the database", id);
        return participantService.viewDetails(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Participant post(@RequestBody @Valid ParticipantModel model) {
        log.info("Adding a new participant to the catalog with name {}", model.name());
        return participantService.add(model);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Deleting participant with id {}", id);
        participantService.remove(id);
    }

    @PutMapping("{id}")
    public Participant put(@PathVariable Long id, @RequestBody @Valid ParticipantModel model) {
        log.info("Updating participant with id {}", id);
        return participantService.editDetails(id, model);
    }
}

