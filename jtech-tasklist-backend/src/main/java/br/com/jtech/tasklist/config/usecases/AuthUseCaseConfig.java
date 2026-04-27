package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.application.core.usecases.LoginUseCase;
import br.com.jtech.tasklist.application.core.usecases.RefreshAccessTokenUseCase;
import br.com.jtech.tasklist.application.core.usecases.RegisterUserUseCase;
import br.com.jtech.tasklist.application.ports.output.PasswordGateway;
import br.com.jtech.tasklist.application.ports.output.RefreshTokenGateway;
import br.com.jtech.tasklist.application.ports.output.TokenGateway;
import br.com.jtech.tasklist.application.ports.output.UserGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserGateway userGateway, PasswordGateway passwordGateway) {
        return new RegisterUserUseCase(userGateway, passwordGateway);
    }

    @Bean
    public LoginUseCase loginUseCase(UserGateway userGateway,
                                     PasswordGateway passwordGateway,
                                     TokenGateway tokenGateway,
                                     RefreshTokenGateway refreshTokenGateway) {
        return new LoginUseCase(userGateway, passwordGateway, tokenGateway, refreshTokenGateway);
    }

    @Bean
    public RefreshAccessTokenUseCase refreshAccessTokenUseCase(RefreshTokenGateway refreshTokenGateway,
                                                               UserGateway userGateway,
                                                               TokenGateway tokenGateway) {
        return new RefreshAccessTokenUseCase(refreshTokenGateway, userGateway, tokenGateway);
    }
}
