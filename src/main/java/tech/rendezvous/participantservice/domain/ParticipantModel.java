package tech.rendezvous.participantservice.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public record ParticipantModel (
        @NotNull(message="The usernames must be defined")
        Set<String> usernames,
        @NotBlank(message = "The name must be defined")
        String name) {
}
