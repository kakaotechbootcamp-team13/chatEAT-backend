package com.chateat.chatEAT.domain.chat.service;

import com.chateat.chatEAT.domain.chat.InputChat;
import com.chateat.chatEAT.domain.chat.repository.InputChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InputChatService {
    private final InputChatRepository inputChatRepository;

    public InputChat saveChat(InputChat inputChat) {
        inputChat.setCreatedAt(new Date());
        return inputChatRepository.save(inputChat);
    }

    public Optional<InputChat> findById(String id) {
        return inputChatRepository.findById(id);
    }

    public List<InputChat> findChatsByEmail(String email) {
        return inputChatRepository.findByEmailOrderByTimestamp(email);
    }
}
