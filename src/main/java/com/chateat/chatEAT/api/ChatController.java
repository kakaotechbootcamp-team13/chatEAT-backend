package com.chateat.chatEAT.api;

import com.chateat.chatEAT.domain.chat.Chat;
import com.chateat.chatEAT.domain.chat.repository.ChatRepository;
import com.chateat.chatEAT.domain.chat.dto.ChatRequest;
import com.chateat.chatEAT.domain.chat.dto.AiRequest;
import com.chateat.chatEAT.domain.chat.dto.AiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatRepository chatRepository;
    private final RestTemplate restTemplate;

    public ChatController(ChatRepository chatRepository, RestTemplate restTemplate) {
        this.chatRepository = chatRepository;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest chatRequest) {
        Chat chat = new Chat();
        chat.setMessage(chatRequest.getMessage());
        chat.setTimestamp(LocalDateTime.now());

        chatRepository.save(chat);

        //AI API로 메시지 전송
        AiRequest aiRequest = new AiRequest(chatRequest.getMessage());
        AiResponse aiResponse = restTemplate.postForObject(
                "http://localhost:8080/mock-ai", aiRequest, AiResponse.class);

        //응답 메시지 저장
        Chat aiChat = new Chat();
        aiChat.setMessage(aiResponse.getMessage());
        aiChat.setTimestamp(LocalDateTime.now());

        chatRepository.save(aiChat);

        //응답 메시지 반환
        return ResponseEntity.ok(aiResponse.getMessage());
    }
}
