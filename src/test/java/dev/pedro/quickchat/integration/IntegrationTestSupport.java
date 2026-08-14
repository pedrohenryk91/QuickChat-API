package dev.pedro.quickchat.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.pedro.quickchat.auth.dto.LoginRequest;
import dev.pedro.quickchat.user.dto.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
abstract class IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    protected String uniqueUsername(String prefix) {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        String username = prefix + suffix;
        return username.length() > 20 ? username.substring(0, 20) : username;
    }

    protected RegisteredUser registerAndLogin(String usernamePrefix, String password) throws Exception {
        String username = uniqueUsername(usernamePrefix);
        CreateUserRequest createRequest = new CreateUserRequest(username, username + "-nick", password, null);

        String createBody = mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String userId = objectMapper.readTree(createBody).get("userId").asText();

        LoginRequest loginRequest = new LoginRequest(username, password);
        String loginBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginBody).get("token").asText();

        return new RegisteredUser(userId, username, token);
    }

    protected record RegisteredUser(String userId, String username, String token) {}
}