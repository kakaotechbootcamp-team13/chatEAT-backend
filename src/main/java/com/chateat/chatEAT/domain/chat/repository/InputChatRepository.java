package com.chateat.chatEAT.domain.chat.repository;

import com.chateat.chatEAT.domain.chat.InputChat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InputChatRepository extends MongoRepository<InputChat, String> {
}
