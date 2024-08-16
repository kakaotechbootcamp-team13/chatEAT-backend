package com.chateat.chatEAT.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiRequest {
    private String prompt;

    public AiRequest() {}

    public AiRequest(String prompt) {
        this.prompt = prompt;
    }
}