package com.chateat.chatEAT.domain.chat;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection="output_chats")
public class OutputChat {
    @Id
    private String id;
    private String message;
    private LocalDateTime timestamp;
    private String email;
    private boolean isBotResponse;
}
