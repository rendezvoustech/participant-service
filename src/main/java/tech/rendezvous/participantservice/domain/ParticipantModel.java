package tech.rendezvous.participantservice.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ParticipantModel (
        @NotNull(message="The usernames must be defined")
        Iterable<String> usernames,
        @NotBlank(message = "The name must be defined")
        String name) {
}
