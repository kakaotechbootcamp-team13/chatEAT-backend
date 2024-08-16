package com.chateat.chatEAT.api;

import com.chateat.chatEAT.auth.dto.AuthDto;
import com.chateat.chatEAT.domain.chat.InputChat;
import com.chateat.chatEAT.domain.chat.OutputChat;
import com.chateat.chatEAT.domain.chat.service.AIService;
import com.chateat.chatEAT.domain.chat.service.ChatService;
import com.chateat.chatEAT.domain.chat.service.OutputChatService;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=",
        "spring.datasource.driver-class-name=",
        "spring.jpa.database-platform=",
        "spring.mongodb.embedded.version=4.0.2",
        "spring.data.mongodb.uri=mongodb://localhost:27017/testdb"
})
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private ChatService chatService;

    @MockBean
    private OutputChatService outputChatService;

    @MockBean
    private AIService aiService;

    @BeforeEach
    public void signUp() throws Exception {
        // 회원가입 요청
        MemberJoinRequest joinRequest = new MemberJoinRequest("test@test.com", "123abc!@#", "nickname");

        mockMvc.perform(post("/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isCreated());

    }

    @AfterEach
    public void deleteUser() {
        memberRepository.deleteAll();
    }

    private Map<String, String> getToken() throws Exception {
        AuthDto.LoginDto loginRequest = new AuthDto.LoginDto("test@test.com", "123abc!@#");
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, String> token = new HashMap<>();
        String accessToken = result.getResponse().getHeader("Authorization");
        String encryptedRefreshToken = result.getResponse().getHeader("Refresh");
        token.put("access_token", accessToken != null ? accessToken.replace("Bearer ", "") : null);
        token.put("refresh_token", encryptedRefreshToken);
        return token;
    }

    @DisplayName("API 호출 및 응답에 성공한다.")
    @Test
    @WithMockUser(username = "testuser")
    public void testChat() throws Exception {
        Map<String, String> token = getToken();
        // Mocking AiResponse
        Mockito.when(aiService.getAIResponse(any(String.class)))
                .thenReturn("Mock AI Response");

        // JSON 데이터 준비
        String jsonRequest = "{\"message\": \"Hello, AI!\"}";

        // POST 요청 수행
        MvcResult result = mockMvc.perform(post("/chat")
                        .header("Authorization", "Bearer " + token.get("access_token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // input에 저장된 데이터 확인
        ArgumentCaptor<InputChat> inputChatCaptor = ArgumentCaptor.forClass(InputChat.class);
        Mockito.verify(chatService).saveChat(inputChatCaptor.capture());
        assertThat(inputChatCaptor.getValue().getMessage()).isEqualTo("Hello, AI!");
        assertThat(inputChatCaptor.getValue().getEmail()).isEqualTo("test@test.com");
        assertThat(inputChatCaptor.getValue().isBotResponse()).isFalse();

        // output에 저장된 데이터 확인
        ArgumentCaptor<OutputChat> outputChatCaptor = ArgumentCaptor.forClass(OutputChat.class);
        Mockito.verify(outputChatService).saveChat(outputChatCaptor.capture());
        assertThat(outputChatCaptor.getValue().getMessage()).isEqualTo("Mock AI Response");
        assertThat(outputChatCaptor.getValue().getEmail()).isEqualTo("test@test.com");
        assertThat(outputChatCaptor.getValue().isBotResponse()).isTrue();

        // 응답 확인
        String responseContent = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertThat(jsonResponse.get("message").asText()).isEqualTo("Mock AI Response");
    }
}
