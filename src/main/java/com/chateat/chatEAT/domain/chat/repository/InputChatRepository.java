package com.chateat.chatEAT.domain.chat.repository;

import com.chateat.chatEAT.domain.chat.InputChat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InputChatRepository extends MongoRepository<InputChat, String> {
    List<InputChat> findByEmailOrderByTimestamp(String email);
}
