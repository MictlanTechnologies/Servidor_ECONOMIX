package sql.auth.security;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    public Integer getRequiredUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("Authentication required");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthenticatedUser user)) {
            throw new InsufficientAuthenticationException("Authentication required");
        }
        return user.userId();
    }
}
