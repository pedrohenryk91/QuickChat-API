package dev.pedro.quickchat.chat.exception;

public class ChatAlreadyHasUserException extends RuntimeException {
    public ChatAlreadyHasUserException(String chatId, String userId) {
        super(String.format("User %s is already a member of Chat %s", userId, chatId));
    }
}
