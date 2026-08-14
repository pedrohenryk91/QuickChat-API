package dev.pedro.quickchat.auth.exception;

public class FailedLoginException extends RuntimeException {
    public FailedLoginException() {
        super("Invalid username or password");
    }
}
