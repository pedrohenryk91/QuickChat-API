package dev.pedro.quickchat.chat.exception;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String id) {
        super("Chat with id " + id + " not found.");
    }
}
