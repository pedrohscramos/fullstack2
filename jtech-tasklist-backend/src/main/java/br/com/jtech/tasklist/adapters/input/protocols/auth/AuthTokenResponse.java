package br.com.jtech.tasklist.adapters.input.protocols.auth;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
