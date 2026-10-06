package dev.pedro.quickchat.websocket;

import dev.pedro.quickchat.config.TokenService;

import java.util.Collections;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import dev.pedro.quickchat.websocket.exception.AccessDeniedException;

@Component
public class AuthChannelInterceptor implements  ChannelInterceptor {

    private final TokenService tokenService;

    public AuthChannelInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override 
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if(authHeader == null || authHeader.isBlank()) {
                throw new AccessDeniedException("Access denied. Authorization header is blank.");
            }

            String token = authHeader.substring("Bearer ".length());
            var decoded = tokenService.validateToken(token);
            if(decoded.isEmpty()) {
                throw new AccessDeniedException("Access denied. Auth Token is empty");
            }

            String userId = decoded.get().userId();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

            accessor.setUser(authentication);
        }
        return message;
    }

}
