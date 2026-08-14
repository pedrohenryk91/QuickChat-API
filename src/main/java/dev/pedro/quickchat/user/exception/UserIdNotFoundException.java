package dev.pedro.quickchat.user.exception;

public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(String id) {
        super("User id '" + id +"' was not found");
    }
}
