package com.chateat.chatEAT.domain.chat.dto;

import com.chateat.chatEAT.domain.chat.OutputChat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String id;
    private String message;
    private LocalDateTime timestamp;
    private String email;
    private boolean isBotResponse;

    public static ChatResponse of(OutputChat chat) {
        return new ChatResponse(chat.getId(), chat.getMessage(), chat.getTimestamp(), chat.getEmail(), chat.isBotResponse());
    }
}
