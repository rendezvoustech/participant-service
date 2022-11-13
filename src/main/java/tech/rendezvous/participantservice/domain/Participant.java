package tech.rendezvous.participantservice.domain;

public record Participant(String id, Iterable<String> usernames, String name) {
}
