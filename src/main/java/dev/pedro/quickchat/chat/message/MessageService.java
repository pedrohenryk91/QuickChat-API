package dev.pedro.quickchat.chat.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.pedro.quickchat.chat.message.dto.MessageResponse;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Page<MessageResponse> getMessagesByChat(String chatId, Pageable pageable) {
        if(chatId.isBlank() || chatId == null) {
            return Page.empty();
        }

        Page<Message> messagePage = messageRepository.findByChatId(chatId, pageable);
        return messagePage.map(message -> new MessageResponse(
            message.getId(),
            message.getContent(),
            message.getAuthor().getUsername(),
            message.getChat().getId(),
            message.getCreatedAt()
        ));
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}
