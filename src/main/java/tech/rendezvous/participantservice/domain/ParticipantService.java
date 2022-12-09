package tech.rendezvous.participantservice.domain;

import org.springframework.stereotype.Service;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Iterable<Participant> viewList() {
        return participantRepository.findAll();
    }

    public Participant viewDetails(Long id) {
        return participantRepository.findById(id).orElseThrow(() -> new ParticipantNotFoundException(id));
    }

    public Participant add(ParticipantModel model) {
        if (!participantRepository.findByUsername(model.username()).isEmpty())
                throw new ParticipantWithUsernameAlreadyExistsException(model.username());
        return participantRepository.save(Participant.of(model.username(), model.name()));
    }

    public void remove(Long id) {
        participantRepository.deleteById(id);
    }

    public Participant editDetails(Long id, ParticipantModel model) {
        return participantRepository.findById(id)
                .map(participant -> {
                    var toUpdate = new Participant(
                            participant.id(),
                            model.username(),
                            model.name(),
                            participant.createdDate(),
                            participant.lastModifiedDate(),
                            participant.version()
                    );
                    return participantRepository.save(toUpdate);
                })
                .orElseGet(() -> add(model));
    }
}
