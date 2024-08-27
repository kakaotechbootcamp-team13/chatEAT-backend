package com.chateat.chatEAT.domain.like;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection="likes")
public class LikeChat {
    @Id
    private String id;
    private String messageId;
    private String messageText;
    private LocalDateTime timestamp;
    private String email;

    public LikeChat(String messageId, String messageText, String email, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.email = email;
        this.timestamp = timestamp;
    }
}
