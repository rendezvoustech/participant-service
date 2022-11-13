package tech.rendezvous.participantservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.rendezvous.participantservice.domain.Participant;
import tech.rendezvous.participantservice.domain.ParticipantModel;
import tech.rendezvous.participantservice.domain.ParticipantService;

import javax.validation.Valid;

@RestController
@RequestMapping("participants")
public class ParticipantRestController {
    private final ParticipantService participantService;

    public ParticipantRestController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping
    public Iterable<Participant> getParticipants() {
        return participantService.viewList();
    }

    @GetMapping("{id}")
    public Participant getParticipant(@PathVariable String id) {
        return participantService.viewDetails(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Participant post(@RequestBody @Valid ParticipantModel model) {
        return participantService.add(model);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        participantService.remove(id);
    }

    @PutMapping("{id}")
    public Participant put(@PathVariable String id, @RequestBody @Valid ParticipantModel model) {
        return participantService.editDetails(id, model);
    }
}

