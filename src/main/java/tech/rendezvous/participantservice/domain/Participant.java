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

        List<String> usernames,

        String name,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate,

        @Version
        int version) {

    public static Participant of(
            List<String> usernames, String name) {
        return new Participant(null, usernames, name, null, null, 0);
    }
}
