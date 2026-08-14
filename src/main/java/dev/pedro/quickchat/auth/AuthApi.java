package dev.pedro.quickchat.auth;

import org.springframework.http.ResponseEntity;

import dev.pedro.quickchat.auth.dto.LoginRequest;
import dev.pedro.quickchat.auth.dto.LoginResponse;
import dev.pedro.quickchat.shared.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface AuthApi {

    @Operation(
        summary = "Login",
        description = "Log an user by returning Authorization Token, expires in 1 day"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ok, user credentials valid. Returns Authorization Token"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request, incorrect request body",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized, bad credentials (incorrect password)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "'username' was not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<LoginResponse> login(LoginRequest request);

}
