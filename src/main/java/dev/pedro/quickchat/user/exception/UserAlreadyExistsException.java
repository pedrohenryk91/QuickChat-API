package dev.pedro.quickchat.user.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super("Username " + username + " already exists, please try a different one");
    }
}
