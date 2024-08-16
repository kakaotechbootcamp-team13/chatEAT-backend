package com.chateat.chatEAT.api;

import com.chateat.chatEAT.domain.chat.dto.AiRequest;
import com.chateat.chatEAT.domain.chat.dto.AiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockAiController {

    @PostMapping("/mock-ai")
    public AiResponse mockAiEndpoint(@RequestBody AiRequest request) {
        AiResponse response = new AiResponse();
        response.setMessage("Mock response for: " + request.getPrompt());
        return response;
    }
}
