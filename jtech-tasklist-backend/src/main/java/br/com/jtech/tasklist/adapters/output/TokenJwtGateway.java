package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.application.ports.output.TokenGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenJwtGateway implements TokenGateway {

    private final SecretKey key;
    private final long accessExpirationSeconds;

    public TokenJwtGateway(@Value("${security.jwt.secret:change-me-super-secret-key-which-has-at-least-32chars}") String secret,
                           @Value("${security.jwt.access-expiration-seconds:3600}") long accessExpirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationSeconds = accessExpirationSeconds;
    }

    @Override
    public String generateAccessToken(UUID userId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("uid", userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessExpirationSeconds)))
                .signWith(key)
                .compact();
    }

    @Override
    public String getEmailFromAccessToken(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public boolean isAccessTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public long accessTokenExpiresInSeconds() {
        return accessExpirationSeconds;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
