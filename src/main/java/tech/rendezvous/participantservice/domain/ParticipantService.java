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

    public Participant viewDetails(String id) {
        return participantRepository.findOne(id).orElseThrow(() -> new ParticipantNotFoundException(id));
    }

    public Participant add(ParticipantModel model) {
        model.usernames().forEach(username -> {
            if (participantRepository.findOneByUsername(username).isPresent())
                throw new ParticipantWithUsernameAlreadyExistsException(username);
        });
        return participantRepository.add(model);
    }

    public void remove(String id) {
        participantRepository.remove(id);
    }

    public Participant editDetails(String id, ParticipantModel model) {
        return participantRepository.findOne(id)
                .map(participant -> {
                    var toUpdate = new Participant(
                            participant.id(),
                            model.usernames(),
                            model.name()
                    );
                    return participantRepository.save(toUpdate);
                })
                .orElseGet(() -> add(model));
    }
}
