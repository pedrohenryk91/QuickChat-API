package dev.pedro.quickchat.chat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import dev.pedro.quickchat.chat.dto.ChatCursor;
import dev.pedro.quickchat.chat.dto.ChatPageResponse;
import dev.pedro.quickchat.chat.dto.ChatResponse;
import dev.pedro.quickchat.chat.dto.CreateGroupChatRequest;
import dev.pedro.quickchat.chat.exception.ChatAlreadyExistsException;
import dev.pedro.quickchat.chat.exception.ChatNotFoundException;
import dev.pedro.quickchat.user.User;
import dev.pedro.quickchat.user.UserService;
import dev.pedro.quickchat.user.exception.UsersNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final UserService userService;

    public ChatService(ChatRepository chatRepository, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.chatRepository = chatRepository;
        this.userService = userService;
    }

    @Transactional
    public Chat findByIdOrThrow(String id) {
        Optional<Chat> chat = chatRepository.findById(id);
        if(chat.isEmpty()) {
            throw new ChatNotFoundException(id);
        }
        return chat.get();
    }

    @Transactional
    public Chat createDirectChat(String userAId, String userBId) {
        var userA = userService.findByIdOrThrow(userAId);
        var userB = userService.findByIdOrThrow(userBId);
        Optional<Chat> alreadyExists = chatRepository.findDirectChatBetweenUsers(userAId, userBId);

        if(alreadyExists.isPresent()) {
            throw new ChatAlreadyExistsException(userA.getUsername(), userB.getUsername());
        }

        Chat chat = new Chat();
        chat.setType(ChatType.DIRECT);
        chat.getMembers().add(userA);
        chat.getMembers().add(userB);

        var response = chatRepository.save(chat);
        response.setName(userB.getNickname());
        response.setIconUrl(userB.getIconUrl());

        return response;
    }

    @Transactional
    public Chat createGroupChat(CreateGroupChatRequest request, String creatorId) {
        Chat chat = new Chat();

        Set<String> allIds = new HashSet<>(request.usersIds());
        allIds.add(creatorId);

        HashSet<User> members = new HashSet<User>(userService.findAllById(allIds));

        if(members.size() < allIds.size()) {
            throw new UsersNotFoundException("One or more users were not found.");
        }

        chat.setType(ChatType.GROUP);
        chat.setName(request.name());
        chat.setMembers(members);
        chat.setIconUrl(request.iconUrl());

        var socketMessage = new ChatResponse(
            chat.getId(), 
            chat.getUpdatedAt(), 
            chat.getIconUrl(), 
            chat.getName(),
            chat.getType().toString()
        );

        chat.getMembers().forEach(user -> messagingTemplate.convertAndSendToUser(
            user.getId(),
            "/queue/events",
            socketMessage
        ));

        return chatRepository.save(chat);
    }

    @Transactional
    public ChatResponse getChatByIdWithName(String userId, String id) {
        try {
            Chat chat = chatRepository.getReferenceById(id);
            if(chat.getType().equals(ChatType.GROUP)) {
                return new ChatResponse(
                    chat.getId(),
                    chat.getUpdatedAt(),
                    chat.getIconUrl(),
                    chat.getName(),
                    chat.getType().toString()
                );
            }
            var otherUser = chat.getMembers().stream()
                    .filter(member -> !member.getId().equals(userId))
                    .findFirst()
                    .orElse(null);
            String name = (otherUser != null) ? otherUser.getNickname() : chat.getName();
            String iconUrl = (otherUser != null) ? otherUser.getIconUrl() : chat.getIconUrl();
            return new ChatResponse(
                id,
                chat.getUpdatedAt(),
                iconUrl,
                name,
                chat.getType().toString()
            );
        } catch (Exception e) {
            throw new ChatNotFoundException(id);
        }
    }

    @Transactional
    public ChatPageResponse getChatsByUser(String userId, String rawCursor, int limit) {
        userService.validateExists(userId);

        ChatCursor cursor = ChatCursor.fromBase64(rawCursor);
    
        LocalDateTime beforeDate = null;
        String beforeId = null;
        if (cursor != null) {
            beforeDate = cursor.beforeDate();
            beforeId = cursor.beforeId().isBlank() ? cursor.beforeId() : null;
        }

        Pageable pageable = PageRequest.of(0, limit + 1);
        var chats = chatRepository.findByMembersId(
            userId,
            beforeDate,
            beforeId,
            pageable
        );

        boolean hasMore = chats.size() > limit;

        if(hasMore) {
            chats = chats.subList(0, limit);
        }
    
        String nextCursor = null;
        if(!chats.isEmpty() && hasMore) {
            Chat lastChat = chats.get(chats.size() - 1);
            nextCursor = new ChatCursor(lastChat.getUpdatedAt(), lastChat.getId()).toBase64();
        }

        List<ChatResponse> response = chats.stream().map(chat -> {
                if(chat.getType().equals(ChatType.GROUP)) {
                    return new ChatResponse(
                        chat.getId(),
                        chat.getUpdatedAt(),
                        chat.getIconUrl(),
                        chat.getName(),
                        chat.getType().toString()
                    );
                }
                var otherUser = chat.getMembers().stream()
                        .filter(member -> !member.getId().equals(userId))
                        .findFirst()
                        .orElse(null);
                String name = (otherUser != null) ? otherUser.getNickname() : chat.getName();
                String iconUrl = (otherUser != null) ? otherUser.getIconUrl() : chat.getIconUrl();

                return new ChatResponse(
                    chat.getId(),
                    chat.getUpdatedAt(),
                    iconUrl,
                    name,
                    chat.getType().toString()
                );
            }).toList();

        return new ChatPageResponse(response, nextCursor, hasMore);
    }

}
