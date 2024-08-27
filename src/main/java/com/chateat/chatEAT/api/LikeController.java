package com.chateat.chatEAT.api;

import com.chateat.chatEAT.auth.principaldetails.PrincipalDetails;
import com.chateat.chatEAT.domain.chat.OutputChat;
import com.chateat.chatEAT.domain.chat.service.OutputChatService;
import com.chateat.chatEAT.domain.like.dto.MessageIdRequest;
import com.chateat.chatEAT.domain.like.repository.LikeChatRepository;
import com.chateat.chatEAT.domain.like.LikeChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
public class LikeController {

    @Autowired
    private LikeChatRepository likeChatRepository;

    @Autowired
    private OutputChatService outputChatService;

    @GetMapping("/likes")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<LikeChat>> getUserLikes(@AuthenticationPrincipal PrincipalDetails user) {
        String userEmail = user.getUsername();
        List<LikeChat> userLikes = likeChatRepository.findByEmail(userEmail);

        return ResponseEntity.ok(userLikes);
    }

    @PostMapping("/like")
    public ResponseEntity<LikeChat> createLike(@RequestBody MessageIdRequest messageIdRequest) {
        String messageId = messageIdRequest.getMessageId();

        Optional<OutputChat> outputChatOptional = outputChatService.findById(messageId);

        if(outputChatOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OutputChat outputChat = outputChatOptional.get();

        LikeChat likeChat = new LikeChat(
                outputChat.getId(),
                outputChat.getMessage(),
                outputChat.getEmail(),
                outputChat.getTimestamp()
        );

        LikeChat savedLikeChat = likeChatRepository.save(likeChat);

        return ResponseEntity.ok(savedLikeChat);
    }

    @DeleteMapping("/like/{messageId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteLike(@PathVariable String messageId, @AuthenticationPrincipal PrincipalDetails user) {
        String userEmail = user.getUsername();
        likeChatRepository.deleteByMessageIdAndEmail(messageId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
