package dev.pedro.quickchat.chat;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.pedro.quickchat.chat.dto.ChatResponse;
import dev.pedro.quickchat.chat.exception.ChatAlreadyExistsException;
import dev.pedro.quickchat.chat.exception.ChatNotFoundException;
import dev.pedro.quickchat.user.UserService;
import jakarta.transaction.Transactional;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;

    public ChatService(ChatRepository chatRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.userService = userService;
    }

    @Transactional
    public Chat createDirectChat(String userAId, String userBId) {
        Optional<Chat> alreadyExists = chatRepository.findDirectChatBetweenUsers(userAId, userBId);
        if(alreadyExists.isPresent()) {
            throw new ChatAlreadyExistsException(userAId, userBId);
        }

        var userA = userService.findByIdOrThrow(userAId);
        var userB = userService.findByIdOrThrow(userBId);

        Chat chat = new Chat();
        chat.setType(ChatType.DIRECT);
        chat.getMembers().add(userA);
        chat.getMembers().add(userB);
    
        return chatRepository.save(chat);
    }

    @Transactional
    public Chat getChatById(String id) {
        try {
            Chat chat = chatRepository.getReferenceById(id);
            return chat;
        } catch (Exception e) {
            throw new ChatNotFoundException(id);
        }
    }

    @Transactional
    public Page<ChatResponse> getChatsByUser(String userId, Pageable pageable) {
        userService.validateExists(userId);
    
        var chats = chatRepository.findByMembersId(userId, pageable);
    
        Page<ChatResponse> responses = chats
            .map(chat -> new ChatResponse(chat.getId(), chat.getCreatedAt(), chat.getUpdatedAt(), chat.getName()));

        return responses;
    }

}
