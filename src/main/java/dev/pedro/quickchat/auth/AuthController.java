package dev.pedro.quickchat.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pedro.quickchat.auth.dto.LoginRequest;
import dev.pedro.quickchat.auth.dto.LoginResponse;
import dev.pedro.quickchat.auth.exception.FailedLoginException;
import dev.pedro.quickchat.config.TokenService;
import dev.pedro.quickchat.user.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("auth")
@SecurityRequirement
public class AuthController implements AuthApi {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.username(), request.password());
            Authentication authentication = authenticationManager.authenticate(userAndPass);
            User user = (User) authentication.getPrincipal();
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (Exception e) {
            throw new FailedLoginException();
        }
    }

}