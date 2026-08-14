package dev.pedro.quickchat.integration;

import dev.pedro.quickchat.auth.dto.LoginRequest;
import dev.pedro.quickchat.user.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerIntegrationTest extends IntegrationTestSupport {

    @Test
    void login_withValidCredentials_returnsToken() throws Exception {
        String username = uniqueUsername("alice");
        String password = "supersecret";

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest(username, "Alice", password, null))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(username, password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_withBlankUsername_returns400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("", "somepass"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withBlankPassword_returns400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("someuser", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withWrongPassword_isUnauthorized() throws Exception {
        String username = uniqueUsername("bob");
        String correctPassword = "correctPassword1";

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest(username, "Bob", correctPassword, null))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(username, "wrongPassword"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withNonExistentUser_isUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest(uniqueUsername("ghost"), "whatever1"))))
                .andExpect(status().isUnauthorized());
    }
}