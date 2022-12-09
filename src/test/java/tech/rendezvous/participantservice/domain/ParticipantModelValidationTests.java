package tech.rendezvous.participantservice.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipantModelValidationTests {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var participant = new ParticipantModel("anders@andeby.dk", "Anders And");
        Set<ConstraintViolation<ParticipantModel>> violations = validator.validate(participant);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenUsernameIsNotDefinedThenValidationFails() {
        var participant = new ParticipantModel(null, "Anders And");
        Set<ConstraintViolation<ParticipantModel>> violations = validator.validate(participant);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The username must be defined");
    }

    @Test
    void whenUsernameIsBlankThenValidationFails() {
        var participant = new ParticipantModel("", "Anders And");
        Set<ConstraintViolation<ParticipantModel>> violations = validator.validate(participant);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The username must be defined");
    }

    @Test
    void whenNameIsNotDefinedThenValidationFails() {
        var participant = new ParticipantModel("anders@andeby.dk", null);
        Set<ConstraintViolation<ParticipantModel>> violations = validator.validate(participant);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The name must be defined");
    }

    @Test
    void whenNameIsBlankThenValidationFails() {
        var participant = new ParticipantModel("anders@andeby.dk", "");
        Set<ConstraintViolation<ParticipantModel>> violations = validator.validate(participant);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The name must be defined");
    }
}
