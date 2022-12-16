package tech.rendezvous.participantservice.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import tech.rendezvous.participantservice.domain.Participant;
import tech.rendezvous.participantservice.domain.ParticipantModel;
import tech.rendezvous.participantservice.domain.ParticipantRepository;

import java.util.Optional;

public class ParticipantMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private ParticipantRepository participantRepository;
    private Object filterObject;
    private Object returnObject;
    private Object target;

    public ParticipantMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /**
     checks if the authenticated user have access to the participant detail with the id
     Only a user have access to his own participant details
     */
    public boolean isParticipant(Long id){
        Optional<Participant> participant =  this.participantRepository.findById(id);
        return participant.isPresent() && participant.get().username().equals(authentication.getName());
    }

    public boolean hasUsername(ParticipantModel participantModel) {
        return (participantModel != null && participantModel.username().equals(authentication.getName()));
    }

    //We need this setter method to set the ParticipantRepository from another class because this one dosen't have access to Application Context.
    public void setParticipantRepository(ParticipantRepository participantRepository){
        this.participantRepository=participantRepository;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }
}

