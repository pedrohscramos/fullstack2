package br.com.jtech.tasklist.application.core.domains;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {
}
