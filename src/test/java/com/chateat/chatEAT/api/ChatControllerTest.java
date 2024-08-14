package com.chateat.chatEAT.api;

import com.chateat.chatEAT.domain.chat.Chat;
import com.chateat.chatEAT.domain.chat.dto.AiRequest;
import com.chateat.chatEAT.domain.chat.dto.AiResponse;
import com.chateat.chatEAT.domain.chat.repository.ChatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @MockBean
    private ChatRepository chatRepository;

    @MockBean
    private RestTemplate restTemplate;

    @DisplayName("API 호출 및 응답에 성공한다.")
    @Test
    public void testChat() throws Exception {
        // Mocking AiResponse
        AiResponse mockAiResponse = new AiResponse();
        mockAiResponse.setMessage("Mock AI Response");
        Mockito.when(restTemplate.postForObject(eq("http://localhost:8080/mock-ai"), any(AiRequest.class), eq(AiResponse.class)))
                .thenReturn(mockAiResponse);

        // JSON 데이터 준비
        String jsonRequest = "{\"message\": \"Hello, AI!\"}";

        // POST 요청 수행
        MvcResult result = mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // DB에 저장된 데이터 확인
        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        Mockito.verify(chatRepository, Mockito.times(2)).save(chatCaptor.capture());
        assertThat(chatCaptor.getAllValues().get(0).getMessage()).isEqualTo("Hello, AI!");
        assertThat(chatCaptor.getAllValues().get(1).getMessage()).isEqualTo("Mock AI Response");

        // 응답 확인
        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isEqualTo("Mock AI Response");
    }
}
