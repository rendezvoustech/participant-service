package tech.rendezvous.participantservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.rendezvous.participantservice.domain.ParticipantNotFoundException;
import tech.rendezvous.participantservice.domain.ParticipantWithUsernameAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ParticipantRestControllerAdvice {

    @ExceptionHandler(ParticipantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String participantNotFoundHandler(ParticipantNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ParticipantWithUsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String participantWithUsernameAlreadyExistsHandler(ParticipantWithUsernameAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
