package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.PasswordGateway;
import br.com.jtech.tasklist.application.ports.output.UserGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;

public class RegisterUserUseCase {

    private final UserGateway userGateway;
    private final PasswordGateway passwordGateway;

    public RegisterUserUseCase(UserGateway userGateway, PasswordGateway passwordGateway) {
        this.userGateway = userGateway;
        this.passwordGateway = passwordGateway;
    }

    public User execute(String name, String email, String password) {
        if (userGateway.existsByEmail(email)) {
            throw new ConflictException("Email already registered");
        }
        String passwordHash = passwordGateway.hash(password);
        return userGateway.save(name.trim(), email.trim().toLowerCase(), passwordHash);
    }
}
