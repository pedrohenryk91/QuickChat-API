package dev.pedro.quickchat.user.exception;

public class UsernameNotFoundException extends RuntimeException{
    public UsernameNotFoundException(String username) {
        super("Username '" + username +"' was not found");
    }
}
