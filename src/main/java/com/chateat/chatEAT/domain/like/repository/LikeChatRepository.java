package com.chateat.chatEAT.domain.like.repository;

import com.chateat.chatEAT.domain.like.LikeChat;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LikeChatRepository extends MongoRepository<LikeChat, String> {

    List<LikeChat> findByEmail(String email);

    void deleteByMessageIdAndEmail(String messageId, String email);
}
