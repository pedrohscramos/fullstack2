package br.com.jtech.tasklist.application.ports.output;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenGateway {

    String create(UUID userId);

    Optional<UUID> findUserIdByToken(String token);

    boolean isValid(String token);
}
