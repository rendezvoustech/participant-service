package tech.rendezvous.participantservice.domain;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(String id) {
        super("A participant with id " + id + " was not found.");
    }
}
