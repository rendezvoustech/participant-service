package tech.rendezvous.participantservice.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTests {
    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    @Test
    void whenParticipantCreateAlreadyExistsThenThrows() {
        var participantUser = "user@user.us";
        var participantToCreate = new ParticipantModel(participantUser, "NN");
        when(participantRepository.existsByUsername(participantUser)).thenReturn(true);
        assertThatThrownBy(() -> participantService.add(participantToCreate))
                .isInstanceOf(ParticipantWithUsernameAlreadyExistsException.class)
                .hasMessage("A participant with username " + participantUser + " already exists.");
    }

    @Test
    void whenParticipantToReadDoesNotExistThenThrows() {
        var id = 123456L;
        when(participantRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> participantService.viewDetails(id))
                .isInstanceOf(ParticipantNotFoundException.class)
                .hasMessage("A participant with id " + id + " was not found.");
    }
}
