package dev.pedro.quickchat.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import dev.pedro.quickchat.chat.dto.ChatResponse;
import dev.pedro.quickchat.chat.dto.CreateDirectChatRequest;
import dev.pedro.quickchat.chat.message.dto.MessageResponse;
import dev.pedro.quickchat.shared.dto.ApiErrorResponse;
import dev.pedro.quickchat.shared.dto.JWTUserData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface ChatApi {

    @Operation(
        summary = "Create a direct chat between two users",
        description = "Creates an direct chat between the logged user (from authorization header) and the other user which is defined by the field 'receiverUserId'"
    )
        @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created with success, retrieves the create chat."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden, request made without Authorization or with a invalid Token. Try log in first.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "'receiverUserId' was not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ChatResponse> createDirectChat(CreateDirectChatRequest request, JWTUserData currentUser);

    @Operation(
        summary = "Gets a chat by it's id",
        description = "Gets a chat by it's id, only the chat, without messages and members"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success, retrieves the found data."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden, request made without Authorization or with a invalid Token. Try log in first.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "'chatdId' was not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ChatResponse> getChat(String id);

    @Operation(
        summary = "Get all chats from a user",
        description = "Get the chats from the logged user (from authorization header). " +
        "Returns the first 20 elements found, use the pagination params to handle the elements. " +
        "Returns a org.springframework.data.domain.Pageable with content type 'ChatResponse' (check 'PageChatResponse' on schemas for more information)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success, retrieves the found data."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden, request made without Authorization or with a invalid Token. Try log in first.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Page<ChatResponse>> getChatsByUser(JWTUserData currentUser, Pageable pageable);

    @Operation(
        summary = "Get the messages from a chat",
        description = "Get the messages from the chat specified in 'chatId'. " +
        "Returns the first 20 elements found, use the pagination params to handle the elements. " +
        "Returns a org.springframework.data.domain.Pageable with content type 'MessageResponse' (check 'PageMessageResponse' on schemas for more information)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success, retrieves the found data."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden, request made without Authorization or with a invalid Token. Try log in first.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "'chatdId' was not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Page<MessageResponse>> getChatMessages(String chatId, Pageable pageable);

}
