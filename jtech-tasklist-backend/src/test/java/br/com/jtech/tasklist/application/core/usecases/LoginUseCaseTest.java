package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.AuthTokens;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.PasswordGateway;
import br.com.jtech.tasklist.application.ports.output.RefreshTokenGateway;
import br.com.jtech.tasklist.application.ports.output.TokenGateway;
import br.com.jtech.tasklist.application.ports.output.UserGateway;
import br.com.jtech.tasklist.config.infra.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordGateway passwordGateway;

    @Mock
    private TokenGateway tokenGateway;

    @Mock
    private RefreshTokenGateway refreshTokenGateway;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User user;

    @BeforeEach
    void setup() {
        user = new User(
                UUID.randomUUID(),
                "Jane",
                "jane@example.com",
                "hash",
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void shouldLoginSuccessfully() {
        when(userGateway.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(passwordGateway.matches("secret123", "hash")).thenReturn(true);
        when(tokenGateway.generateAccessToken(any(), any())).thenReturn("jwt");
        when(refreshTokenGateway.create(user.id())).thenReturn("refresh");
        when(tokenGateway.accessTokenExpiresInSeconds()).thenReturn(3600L);

        AuthTokens result = loginUseCase.execute("jane@example.com", "secret123");

        assertEquals("jwt", result.accessToken());
        assertEquals("refresh", result.refreshToken());
        assertEquals(3600L, result.expiresInSeconds());
    }

    @Test
    void shouldThrowUnauthorizedWhenPasswordIsInvalid() {
        when(userGateway.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(passwordGateway.matches("wrong", "hash")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> loginUseCase.execute("jane@example.com", "wrong"));
    }
}
