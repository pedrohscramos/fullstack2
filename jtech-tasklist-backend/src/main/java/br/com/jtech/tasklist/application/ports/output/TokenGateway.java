package br.com.jtech.tasklist.application.ports.output;

import java.util.UUID;

public interface TokenGateway {

    String generateAccessToken(UUID userId, String email);

    String getEmailFromAccessToken(String token);

    boolean isAccessTokenValid(String token);

    long accessTokenExpiresInSeconds();
}
