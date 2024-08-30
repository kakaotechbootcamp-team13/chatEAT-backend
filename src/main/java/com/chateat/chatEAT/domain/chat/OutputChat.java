package com.chateat.chatEAT.domain.chat;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "output_chats")
public class OutputChat {
    @Id
    private String id;
    private String message;
    private String email;
    private LocalDateTime timestamp;
    private boolean isBotResponse;
}
