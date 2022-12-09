package tech.rendezvous.participantservice.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.time.Instant;
import java.util.List;

public record Participant(
        @Id
        Long id,

        String username,

        String name,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate,

        @Version
        int version) {

    public static Participant of(
            String username, String name) {
        return new Participant(null, username, name, null, null, 0);
    }
}
