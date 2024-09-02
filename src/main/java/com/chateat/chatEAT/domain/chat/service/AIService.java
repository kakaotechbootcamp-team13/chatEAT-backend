package com.chateat.chatEAT.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${serverUri.aiServer}")
    private String apiUrl;

    public String getAIResponse(String userMessage, @RequestHeader("Authorization") String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);


        Map<String, String> jsonMessage = new HashMap<>();
        jsonMessage.put("prompt", userMessage);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(jsonMessage, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, String> responseBody = objectMapper.readValue(response.getBody(), Map.class);
                return responseBody.get("message");
            } else {
                return "Unexpected response from AI API: " + response.getStatusCode();
            }
        } catch (HttpClientErrorException e) {
            return "Error during AI API request: " + e.getMessage();
        } catch (JsonProcessingException e) {
            return "Error processing AI response";
        }
    }
}
