package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.application.ports.output.PasswordGateway;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordBCryptGateway implements PasswordGateway {

    private final PasswordEncoder passwordEncoder;

    public PasswordBCryptGateway(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String raw) {
        return passwordEncoder.encode(raw);
    }

    @Override
    public boolean matches(String raw, String hash) {
        return passwordEncoder.matches(raw, hash);
    }
}
