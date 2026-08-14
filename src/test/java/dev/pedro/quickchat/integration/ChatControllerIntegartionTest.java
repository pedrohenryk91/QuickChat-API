package dev.pedro.quickchat.integration;

import dev.pedro.quickchat.chat.dto.CreateDirectChatRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChatControllerIntegrationTest extends IntegrationTestSupport {

    @Test
    void createDirectChat_returnsCreatedChatWithoutName() throws Exception {
        RegisteredUser userA = registerAndLogin("hank", "password123");
        RegisteredUser userB = registerAndLogin("iris", "password123");

        mockMvc.perform(post("/chat/create/direct")
                        .header("Authorization", "Bearer " + userA.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDirectChatRequest(userB.userId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.name").doesNotExist());
    }

    @Test
    void createDirectChat_withoutAuth_isForbidden() throws Exception {
        RegisteredUser userB = registerAndLogin("jack", "password123");

        mockMvc.perform(post("/chat/create/direct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDirectChatRequest(userB.userId()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createDirectChat_withBlankReceiverId_returns400() throws Exception {
        RegisteredUser userA = registerAndLogin("karen", "password123");

        mockMvc.perform(post("/chat/create/direct")
                        .header("Authorization", "Bearer " + userA.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDirectChatRequest(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getChatById_returnsTheChat() throws Exception {
        RegisteredUser userA = registerAndLogin("leo", "password123");
        RegisteredUser userB = registerAndLogin("mia", "password123");

        String chatId = createDirectChat(userA, userB);

        mockMvc.perform(get("/chat/{id}", chatId)
                        .header("Authorization", "Bearer " + userA.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(chatId));
    }

    @Test
    void getChatById_withUnknownId_returns404() throws Exception {
        RegisteredUser userA = registerAndLogin("nina", "password123");

        mockMvc.perform(get("/chat/{id}", "does-not-exist")
                        .header("Authorization", "Bearer " + userA.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getChatById_withoutAuth_isForbidden() throws Exception {
        RegisteredUser userA = registerAndLogin("otto", "password123");
        RegisteredUser userB = registerAndLogin("piper", "password123");

        String chatId = createDirectChat(userA, userB);

        mockMvc.perform(get("/chat/{id}", chatId))
                .andExpect(status().isForbidden());
    }

    @Test
    void getChatsByUser_includesCreatedChat() throws Exception {
        RegisteredUser userA = registerAndLogin("oscar", "password123");
        RegisteredUser userB = registerAndLogin("paula", "password123");

        String chatId = createDirectChat(userA, userB);

        mockMvc.perform(get("/chat/user")
                        .header("Authorization", "Bearer " + userA.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id == '" + chatId + "')]").exists());
    }

    @Test
    void getChatMessages_forFreshChat_returnsEmptyPage() throws Exception {
        RegisteredUser userA = registerAndLogin("quinn", "password123");
        RegisteredUser userB = registerAndLogin("rosa", "password123");

        String chatId = createDirectChat(userA, userB);

        mockMvc.perform(get("/chat/{chatId}/messages", chatId)
                        .header("Authorization", "Bearer " + userA.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    private String createDirectChat(RegisteredUser creator, RegisteredUser receiver) throws Exception {
        String body = mockMvc.perform(post("/chat/create/direct")
                        .header("Authorization", "Bearer " + creator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDirectChatRequest(receiver.userId()))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(body).get("id").asText();
    }
}