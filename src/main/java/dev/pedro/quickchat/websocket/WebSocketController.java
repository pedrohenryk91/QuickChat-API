package dev.pedro.quickchat.websocket;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import dev.pedro.quickchat.message.MessageService;
import dev.pedro.quickchat.message.dto.SendMessageRequest;

@Controller
public class WebSocketController {

    private final MessageService messageService;

    public WebSocketController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("queue/messages")
    public void processAndSendMessage(@Payload SendMessageRequest request, Principal principal) {
        String senderId = principal.getName();
        messageService.saveAndSendMessage(request, senderId);
    }

}
