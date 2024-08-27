package com.chateat.chatEAT.domain.like.repository;

import com.chateat.chatEAT.domain.like.LikeChat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LikeChatRepository extends MongoRepository<LikeChat, String> {
    List<LikeChat> findByEmail(String email);
    void deleteByMessageIdAndEmail(String messageId, String email);
}