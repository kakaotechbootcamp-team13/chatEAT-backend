package com.chateat.chatEAT.api;

import com.chateat.chatEAT.auth.principaldetails.PrincipalDetails;
import com.chateat.chatEAT.domain.chat.InputChat;
import com.chateat.chatEAT.domain.chat.OutputChat;
import com.chateat.chatEAT.domain.chat.service.AIService;
import com.chateat.chatEAT.domain.chat.service.ChatService;
import com.chateat.chatEAT.domain.chat.service.OutputChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final OutputChatService outputChatService;
    private final AIService aiService;

    @PostMapping("/chat")
    public OutputChat saveChat(@RequestBody Map<String, String> request, @AuthenticationPrincipal PrincipalDetails user) {
        String message = request.get("message");

        InputChat userChat = new InputChat();
        userChat.setMessage(message);
        userChat.setTimestamp(LocalDateTime.now());
        userChat.setEmail(user.getUsername());
        userChat.setBotResponse(false);
        chatService.saveChat(userChat);


        //AI API로 메시지 전송
        String aiResponseMessage = aiService.getAIResponse(message);

        //응답 메시지 저장
        OutputChat aiChat = new OutputChat();
        aiChat.setMessage(aiResponseMessage);
        aiChat.setTimestamp(LocalDateTime.now());
        aiChat.setEmail(user.getUsername());
        aiChat.setBotResponse(true);
        outputChatService.saveChat(aiChat);

        //응답 메시지 반환
        return aiChat;
    }
}
