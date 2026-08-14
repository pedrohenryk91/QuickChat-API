package dev.pedro.quickchat.chat.exception;

public class ChatAlreadyExistsException extends RuntimeException {
    public ChatAlreadyExistsException(String idA, String idB) {
        super(String.format("Users %s and %s are already in a direct chat", idA, idB));
    }
}
