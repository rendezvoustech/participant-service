package tech.rendezvous.participantservice.api;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import tech.rendezvous.participantservice.domain.Participant;
import tech.rendezvous.participantservice.domain.ParticipantModel;
import tech.rendezvous.participantservice.domain.ParticipantService;

import java.util.Collections;

@Controller
public class ParticipantGraphQLController {
    private final ParticipantService participantService;

    public ParticipantGraphQLController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @QueryMapping()
    public Iterable<Participant> participants() {
        return participantService.viewList();
    }

    @MutationMapping()
    public Participant createParticipant(@Argument String username, @Argument String name) {
        return participantService.add(new ParticipantModel(Collections.singletonList(username), name));
    }
}
