package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.output.repositories.RefreshTokenRepository;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.RefreshTokenEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.ports.output.RefreshTokenGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenJpaGateway implements RefreshTokenGateway {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final long expirationSeconds;

    public RefreshTokenJpaGateway(RefreshTokenRepository refreshTokenRepository,
                                  UserRepository userRepository,
                                  @Value("${security.jwt.refresh-expiration-seconds:1209600}") long expirationSeconds) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String create(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String token = UUID.randomUUID() + "." + UUID.randomUUID();

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setToken(token);
        entity.setExpiresAt(Instant.now().plusSeconds(expirationSeconds));
        refreshTokenRepository.save(entity);
        return token;
    }

    @Override
    public Optional<UUID> findUserIdByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .map(rt -> rt.getUser().getId());
    }

    @Override
    public boolean isValid(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .isPresent();
    }
}
