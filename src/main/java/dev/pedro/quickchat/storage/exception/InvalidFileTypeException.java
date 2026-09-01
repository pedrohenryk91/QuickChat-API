package dev.pedro.quickchat.storage.exception;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String expectedTypes, String received) {
        super(String.format("Invalid file type. Expected %s received %s", expectedTypes, received));
    }
}
