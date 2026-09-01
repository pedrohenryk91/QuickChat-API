package dev.pedro.quickchat.integration;

import dev.pedro.quickchat.user.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIntegrationTest extends IntegrationTestSupport {

    @Test
    void createUser_withValidData_returns201AndBody() throws Exception {
        String username = uniqueUsername("carol");

        mockMvc.perform(multipart("/user/create")
                .param("username", username)
                .param("nickname", "Carlol")
                .param("password", "123456")
                .contentType(MediaType.MULTIPART_FORM_DATA)
            ).andExpect(status().isCreated());
    }

    @Test
    void createUser_withBlankUsername_returns400() throws Exception {
        mockMvc.perform(multipart("/user/create")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("username", "")
                        .param("nickname", "Nick")
                        .param("password", "password123")
                    )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_withUsernameShorterThanThreeChars_returns400() throws Exception {
        mockMvc.perform(multipart("/user/create")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest("ab", "Nick", "password123", null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_withBlankNickname_returns400() throws Exception {
        mockMvc.perform(multipart("/user/create")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest(uniqueUsername("erin"), "", "password123", null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_withPasswordShorterThanSixChars_returns400() throws Exception {
        mockMvc.perform(multipart("/user/create")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest(uniqueUsername("dave"), "Dave", "123", null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_withDuplicateUsername_isRejected() throws Exception {
        String username = uniqueUsername("frank");
        CreateUserRequest request = new CreateUserRequest(username, "Frank", "password123", null);

        mockMvc.perform(multipart("/user/create")
                        .param("username",username)
                        .param("nickname", request.nickname())
                        .param("password", request.password())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                .andExpect(status().isCreated());

        mockMvc.perform(multipart("/user/create")
                        .param("username",username)
                        .param("nickname", request.nickname())
                        .param("password", request.password())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void searchUsers_withoutAuth_isForbidden() throws Exception {
        mockMvc.perform(get("/user/search").param("query", "any"))
                .andExpect(status().isForbidden());
    }

    @Test
    void searchUsers_withAuth_findsMatchingUser() throws Exception {
        RegisteredUser user = registerAndLogin("gina", "password123");

        mockMvc.perform(get("/user/search")
                        .param("query", user.username())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.username == '" + user.username() + "')]").exists());
    }

    @Test
    void searchUsers_withInvalidToken_isForbidden() throws Exception {
        mockMvc.perform(get("/user/search")
                        .param("query", "any")
                        .header("Authorization", "Bearer not-a-real-token"))
                .andExpect(status().isForbidden());
    }
}