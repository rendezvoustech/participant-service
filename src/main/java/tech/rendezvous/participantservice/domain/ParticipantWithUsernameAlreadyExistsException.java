package tech.rendezvous.participantservice.domain;

public class ParticipantWithUsernameAlreadyExistsException extends RuntimeException{
    public ParticipantWithUsernameAlreadyExistsException(String username) {
        super("A participant with username " + username + " already exists.");
    }
}

