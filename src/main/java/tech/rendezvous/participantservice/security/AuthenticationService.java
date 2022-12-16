package tech.rendezvous.participantservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {
    public static final String ROLE_ADMINISTRATOR = "ROLE_administrator";
    public static final String ROLE_USER = "ROLE_user";

    public boolean isAdministrator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(ROLE_ADMINISTRATOR));
    }

    public boolean isAUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(ROLE_USER));
    }

    public String username(Authentication authentication) {
        return authentication.getName();
    }
}
