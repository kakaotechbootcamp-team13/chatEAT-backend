package com.chateat.chatEAT.domain.chat.repository;

import com.chateat.chatEAT.domain.chat.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {
}
