package com.chateat.chatEAT.domain.chat;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

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

    @Indexed(expireAfterSeconds = 604800) // 1주일 후 만료 (7일 * 24시간 * 60분 * 60초)
    private Date createdAt;
}
