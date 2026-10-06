package dev.pedro.quickchat.message;

import dev.pedro.quickchat.chat.ChatService;
import dev.pedro.quickchat.user.UserService;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import dev.pedro.quickchat.message.dto.MessageResponse;
import dev.pedro.quickchat.message.dto.SendMessageRequest;
import jakarta.transaction.Transactional;

@Service
public class MessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;

    public MessageService(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository, UserService userService, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.chatService = chatService;
    }

    @Transactional
    public List<MessageResponse> getMessagesByChat(String chatId, Optional<Long> beforeId, Pageable pageable) {
        if(chatId.isBlank() || chatId == null) {
            return List.of();
        }

        List<Message> messagePage = messageRepository.findByChatId(chatId, beforeId.orElse(null), pageable);

        return messagePage.stream().map(message -> new MessageResponse(
            message.getId(),
            message.getChat().getId(),
            message.getContent(),
            message.getAuthor().getId(),
            message.getAuthor().getUsername(),
            message.getCreatedAt(),
            null
        )).toList();
    }

    @Transactional
    public void saveAndSendMessage(SendMessageRequest request, String authorId) {
        var author = userService.findByIdOrThrow(authorId);
        var chat = chatService.findByIdOrThrow(request.chatId());

        Message message = new Message();
        message.setAuthor(author);
        message.setChat(chat);
        message.setContent(request.content());

        Message savedMessage = messageRepository.save(message);
        MessageResponse response = new MessageResponse(
            savedMessage.getId(),
            savedMessage.getChat().getId(),
            savedMessage.getContent(),
            savedMessage.getAuthor().getId(),
            savedMessage.getAuthor().getUsername(),
            savedMessage.getCreatedAt(),
            request.tempId()
        );

        chat.getMembers().forEach((member) -> messagingTemplate
            .convertAndSendToUser(
                member.getId(),
                "/queue/messages",
                response
            ));
    }
}
