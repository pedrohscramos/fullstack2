package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.auth.AuthTokenResponse;
import br.com.jtech.tasklist.adapters.input.protocols.auth.LoginRequest;
import br.com.jtech.tasklist.adapters.input.protocols.auth.RefreshTokenRequest;
import br.com.jtech.tasklist.adapters.input.protocols.auth.RegisterRequest;
import br.com.jtech.tasklist.adapters.input.protocols.auth.UserResponse;
import br.com.jtech.tasklist.application.core.domains.AuthTokens;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.core.usecases.LoginUseCase;
import br.com.jtech.tasklist.application.core.usecases.RefreshAccessTokenUseCase;
import br.com.jtech.tasklist.application.core.usecases.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          RefreshAccessTokenUseCase refreshAccessTokenUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshAccessTokenUseCase = refreshAccessTokenUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUserUseCase.execute(request.name(), request.email(), request.password());
        UserResponse response = new UserResponse(user.id(), user.name(), user.email(), user.createdAt(), user.updatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthTokens tokens = loginUseCase.execute(request.email(), request.password());
        return ResponseEntity.ok(new AuthTokenResponse(tokens.accessToken(), tokens.refreshToken(), "Bearer", tokens.expiresInSeconds()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthTokens tokens = refreshAccessTokenUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(new AuthTokenResponse(tokens.accessToken(), tokens.refreshToken(), "Bearer", tokens.expiresInSeconds()));
    }
}
