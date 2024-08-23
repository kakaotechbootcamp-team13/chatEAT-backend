package com.chateat.chatEAT.api;

import com.chateat.chatEAT.auth.principaldetails.PrincipalDetails;
import com.chateat.chatEAT.domain.chat.InputChat;
import com.chateat.chatEAT.domain.chat.OutputChat;
import com.chateat.chatEAT.domain.chat.dto.ChatRequest;
import com.chateat.chatEAT.domain.chat.dto.ChatResponse;
import com.chateat.chatEAT.domain.chat.service.AIService;
import com.chateat.chatEAT.domain.chat.service.InputChatService;
import com.chateat.chatEAT.domain.chat.service.OutputChatService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final InputChatService inputChatService;
    private final OutputChatService outputChatService;
    private final AIService aiService;

    @PostMapping("/chat")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ChatResponse> saveChat(@RequestBody ChatRequest request,
                                                 @AuthenticationPrincipal PrincipalDetails user,
                                                 @RequestHeader("Authorization") String authToken) {
        String message = request.message();

        InputChat userChat = new InputChat();
        userChat.setMessage(message);
        userChat.setTimestamp(LocalDateTime.now());
        userChat.setEmail(user.getUsername());
        userChat.setBotResponse(false);
        inputChatService.saveChat(userChat);

        //AI API로 메시지 전송
        String aiResponseMessage = aiService.getAIResponse(message, authToken);

        //응답 메시지 저장
        OutputChat aiChat = new OutputChat();
        aiChat.setMessage(aiResponseMessage);
        aiChat.setTimestamp(LocalDateTime.now());
        aiChat.setEmail(user.getUsername());
        aiChat.setBotResponse(true);
        ChatResponse response = outputChatService.saveChat(aiChat);

        //응답 메시지 반환
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/chats")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<ChatResponse>> getAllChats(@AuthenticationPrincipal PrincipalDetails user) {
        String userEmail = user.getUsername();

        List<InputChat> inputChats = inputChatService.findChatsByEmail(userEmail);
        List<OutputChat> outputChats = outputChatService.findChatsByEmail(userEmail);

        List<ChatResponse> chatResponses = new ArrayList<>();

        inputChats.forEach(chat -> chatResponses.add(new ChatResponse(
                chat.getId(),
                chat.getMessage(),
                chat.getTimestamp(),
                chat.getEmail(),
                chat.isBotResponse())));
        outputChats.forEach(chat -> chatResponses.add(new ChatResponse(
                chat.getId(),
                chat.getMessage(),
                chat.getTimestamp(),
                chat.getEmail(),
                chat.isBotResponse())));

        chatResponses.sort(Comparator.comparing(ChatResponse::getTimestamp));

        return ResponseEntity.status(HttpStatus.OK).body(chatResponses);
    }
}
