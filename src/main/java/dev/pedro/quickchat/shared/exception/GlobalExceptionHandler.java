package dev.pedro.quickchat.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.pedro.quickchat.auth.exception.FailedLoginException;
import dev.pedro.quickchat.chat.exception.ChatAlreadyExistsException;
import dev.pedro.quickchat.chat.exception.ChatNotFoundException;
import dev.pedro.quickchat.shared.dto.ApiErrorResponse;
import dev.pedro.quickchat.storage.exception.UploadFileException;
import dev.pedro.quickchat.user.exception.UserAlreadyExistsException;
import dev.pedro.quickchat.user.exception.UserIdNotFoundException;
import dev.pedro.quickchat.user.exception.UsernameNotFoundException;
import dev.pedro.quickchat.user.exception.UsersNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserIdNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UsersNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUsersNotFound(UsersNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleChatNotFound(ChatNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleChatAlreadyExists(ChatAlreadyExistsException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UploadFileException.class)
    public ResponseEntity<ApiErrorResponse> handleUploadFileException(UploadFileException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.BAD_REQUEST.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.BAD_REQUEST.value(),
            errorMessage,
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(FailedLoginException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(FailedLoginException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> internalServerError(RuntimeException ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        System.err.println("Unhandled Exception: " + ex.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> internalServerError(Exception ex, HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.simple(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        System.err.println("Unhandled Exception: " + ex.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}