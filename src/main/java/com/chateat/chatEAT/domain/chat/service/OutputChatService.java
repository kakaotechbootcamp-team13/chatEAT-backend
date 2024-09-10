package com.chateat.chatEAT.domain.chat.service;

import com.chateat.chatEAT.domain.chat.OutputChat;
import com.chateat.chatEAT.domain.chat.dto.ChatResponse;
import com.chateat.chatEAT.domain.chat.repository.OutputChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutputChatService {

    private final OutputChatRepository outputChatRepository;

    public ChatResponse saveChat(OutputChat outputChat) {
        outputChat.setCreatedAt(new Date());
        outputChatRepository.save(outputChat);
        return ChatResponse.of(outputChat);
    }

    public Optional<OutputChat> findById(String id) {
        return outputChatRepository.findById(id);
    }

    public List<OutputChat> findChatsByEmail(String email) {
        return outputChatRepository.findByEmailOrderByTimestamp(email);
    }
}
