package tech.rendezvous.participantservice.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public record ParticipantModel (
        @NotBlank(message="The username must be defined")
        String username,
        @NotBlank(message = "The name must be defined")
        String name) {
}
