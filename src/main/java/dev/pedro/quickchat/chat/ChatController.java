package dev.pedro.quickchat.chat;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pedro.quickchat.chat.dto.ChatResponse;
import dev.pedro.quickchat.chat.dto.CreateDirectChatRequest;
import dev.pedro.quickchat.chat.message.MessageService;
import dev.pedro.quickchat.chat.message.dto.MessageResponse;
import dev.pedro.quickchat.shared.dto.JWTUserData;
import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("chat")
public class ChatController implements ChatApi{

    private final ChatService chatService;
    private final MessageService messageService;

    public ChatController(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @Override
    @PostMapping("/create/direct")
    public ResponseEntity<ChatResponse> createDirectChat(
        @Valid @RequestBody CreateDirectChatRequest request,
        @AuthenticationPrincipal JWTUserData currentUser
    ) {
        Chat chat = chatService.createDirectChat(currentUser.userId(), request.receiverUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ChatResponse(chat.getId(), chat.getCreatedAt(), chat.getUpdatedAt(), null));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ChatResponse> getChat(@PathVariable("id") String id) {
        Chat chat = chatService.getChatById(id);
        return ResponseEntity.ok(new ChatResponse(chat.getId(), chat.getCreatedAt(), chat.getUpdatedAt(), chat.getName()));
    }

    @Override
    @GetMapping("/user")
    public ResponseEntity<Page<ChatResponse>> getChatsByUser(
        @AuthenticationPrincipal JWTUserData currentUser,
        @PageableDefault(page = 0, size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable
    ) {
        var chats = chatService.getChatsByUser(currentUser.userId(), pageable);
        return ResponseEntity.ok(chats);
    }

    @Override
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Page<MessageResponse>> getChatMessages(
        @PathVariable("chatId") String chatId,
        @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable
    ) {
        var msgs = messageService.getMessagesByChat(chatId, pageable);
        return ResponseEntity.ok(msgs);
    }
}
