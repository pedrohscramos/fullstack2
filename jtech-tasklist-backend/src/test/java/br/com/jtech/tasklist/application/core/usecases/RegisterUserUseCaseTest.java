package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.PasswordGateway;
import br.com.jtech.tasklist.application.ports.output.UserGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordGateway passwordGateway;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private User savedUser;

    @BeforeEach
    void setup() {
        savedUser = new User(
                UUID.randomUUID(),
                "John",
                "john@example.com",
                "hash",
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userGateway.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordGateway.hash("secret123")).thenReturn("hash");
        when(userGateway.save(eq("John"), eq("john@example.com"), eq("hash"))).thenReturn(savedUser);

        User result = registerUserUseCase.execute("John", "john@example.com", "secret123");

        assertEquals(savedUser.id(), result.id());
        assertEquals("john@example.com", result.email());
        verify(passwordGateway).hash("secret123");
        verify(userGateway).save(eq("John"), eq("john@example.com"), anyString());
    }

    @Test
    void shouldThrowConflictWhenEmailAlreadyExists() {
        when(userGateway.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> registerUserUseCase.execute("John", "john@example.com", "secret123"));
    }
}
