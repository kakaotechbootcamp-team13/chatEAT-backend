package com.chateat.chatEAT.domain.chat.service;

import com.chateat.chatEAT.domain.chat.InputChat;
import com.chateat.chatEAT.domain.chat.repository.OutputChatRepository;
import com.chateat.chatEAT.domain.chat.OutputChat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutputChatService {

    private final OutputChatRepository outputChatRepository;

    public OutputChat saveChat(OutputChat outputChat) {
        return outputChatRepository.save(outputChat);
    }

    public Optional<OutputChat> findById(String id) {
        return outputChatRepository.findById(id);
    }
}
