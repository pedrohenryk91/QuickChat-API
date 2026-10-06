package dev.pedro.quickchat.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import dev.pedro.quickchat.shared.dto.ApiErrorResponse;
import dev.pedro.quickchat.shared.dto.JWTUserData;
import dev.pedro.quickchat.user.dto.CreateUserRequest;
import dev.pedro.quickchat.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface UserApi {

    public ResponseEntity<UserResponse> me(JWTUserData currentUser);

    @SecurityRequirement
    @Operation(
        summary = "Create a new user",
        description = "Register a new user on the system, receiving an username, nickname, a password and, optionally, the iconUrl"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created with success"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request error, incorrect request body.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username is already in use",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<UserResponse> createUser(CreateUserRequest request);

    @Operation(
        summary = "Search for users. Requires authentication",
        description = "Search for users looking for their username or nickname"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success. " +
                "Returns the first 20 elements found, use the pagination params to handle the elements. " +
                "Returns a org.springframework.data.domain.Pageable with content type 'UserResponse' (check 'PageUserResponse' on schemas for more information)"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request error, incorrect request body",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Page<UserResponse>> searchUsers(String query, Pageable pageable);

    @Operation(
        summary = "Verifies if an username is already in use",
        description = "Verifies if an username is already in use"
    )
    public ResponseEntity<Boolean> usernameAlreadyInUse(String query);

}
