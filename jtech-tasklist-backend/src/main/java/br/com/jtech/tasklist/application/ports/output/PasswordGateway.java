package br.com.jtech.tasklist.application.ports.output;

public interface PasswordGateway {

    String hash(String raw);

    boolean matches(String raw, String hash);
}
