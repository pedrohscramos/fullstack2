package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.AuthTokens;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.RefreshTokenGateway;
import br.com.jtech.tasklist.application.ports.output.TokenGateway;
import br.com.jtech.tasklist.application.ports.output.UserGateway;
import br.com.jtech.tasklist.config.infra.exceptions.UnauthorizedException;

import java.util.UUID;

public class RefreshAccessTokenUseCase {

    private final RefreshTokenGateway refreshTokenGateway;
    private final UserGateway userGateway;
    private final TokenGateway tokenGateway;

    public RefreshAccessTokenUseCase(RefreshTokenGateway refreshTokenGateway,
                                     UserGateway userGateway,
                                     TokenGateway tokenGateway) {
        this.refreshTokenGateway = refreshTokenGateway;
        this.userGateway = userGateway;
        this.tokenGateway = tokenGateway;
    }

    public AuthTokens execute(String refreshToken) {
        if (!refreshTokenGateway.isValid(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        UUID userId = refreshTokenGateway.findUserIdByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        String newAccessToken = tokenGateway.generateAccessToken(user.id(), user.email());
        return new AuthTokens(newAccessToken, refreshToken, tokenGateway.accessTokenExpiresInSeconds());
    }
}
