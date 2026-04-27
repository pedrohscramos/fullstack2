package br.com.jtech.tasklist.config.security;

import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.UserGateway;
import br.com.jtech.tasklist.config.infra.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserProvider {

    private final UserGateway userGateway;

    public CurrentUserProvider(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        User user = userGateway.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));
        return user.id();
    }
}
