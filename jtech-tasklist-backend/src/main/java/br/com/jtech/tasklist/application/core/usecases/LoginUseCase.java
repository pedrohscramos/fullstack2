package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.AuthTokens;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.PasswordGateway;
import br.com.jtech.tasklist.application.ports.output.RefreshTokenGateway;
import br.com.jtech.tasklist.application.ports.output.TokenGateway;
import br.com.jtech.tasklist.application.ports.output.UserGateway;
import br.com.jtech.tasklist.config.infra.exceptions.UnauthorizedException;

public class LoginUseCase {

    private final UserGateway userGateway;
    private final PasswordGateway passwordGateway;
    private final TokenGateway tokenGateway;
    private final RefreshTokenGateway refreshTokenGateway;

    public LoginUseCase(UserGateway userGateway,
                        PasswordGateway passwordGateway,
                        TokenGateway tokenGateway,
                        RefreshTokenGateway refreshTokenGateway) {
        this.userGateway = userGateway;
        this.passwordGateway = passwordGateway;
        this.tokenGateway = tokenGateway;
        this.refreshTokenGateway = refreshTokenGateway;
    }

    public AuthTokens execute(String email, String password) {
        User user = userGateway.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordGateway.matches(password, user.passwordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = tokenGateway.generateAccessToken(user.id(), user.email());
        String refreshToken = refreshTokenGateway.create(user.id());

        return new AuthTokens(accessToken, refreshToken, tokenGateway.accessTokenExpiresInSeconds());
    }
}
