package com.chateat.chatEAT.domain.chat.service;

import com.chateat.chatEAT.domain.chat.InputChat;
import com.chateat.chatEAT.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public InputChat saveChat(InputChat inputChat) {
        return chatRepository.save(inputChat);
    }

    public Optional<InputChat> findById(String id) {
        return chatRepository.findById(id);
    }
}
