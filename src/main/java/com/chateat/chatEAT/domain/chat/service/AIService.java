package com.chateat.chatEAT.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${API_URL}")
    private String apiUrl;
    
    public AIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    public String getAIResponse(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> jsonMessage = new HashMap<>();
        jsonMessage.put("prompt", userMessage);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(jsonMessage, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        try {
            Map<String, String> responseBody = objectMapper.readValue(response.getBody(), Map.class);
            return responseBody.get("message");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error processing AI response";
        }
    }
}
