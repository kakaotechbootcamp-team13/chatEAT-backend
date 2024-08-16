package com.chateat.chatEAT.domain.chat.repository;

import com.chateat.chatEAT.domain.chat.InputChat;
import com.chateat.chatEAT.domain.chat.OutputChat;
import com.chateat.chatEAT.domain.chat.service.ChatService;
import com.chateat.chatEAT.domain.chat.service.OutputChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xmlunit.builder.Input;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=",
        "spring.datasource.driver-class-name=",
        "spring.jpa.database-platform=",
        "spring.mongodb.embedded.version=4.0.2",
        "spring.data.mongodb.uri=mongodb://localhost:27017/testdb"
})
public class ChatRepositoryTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private OutputChatService outputChatService;

    @DisplayName("Mongodb_intput")
    @Test
    public void testSaveChatToMongoDB() {
        // Given
        InputChat chat = new InputChat();
        chat.setMessage("Hello, MongoDB!");
        chat.setTimestamp(LocalDateTime.now());

        // When
        InputChat savedChat = chatService.saveChat(chat);

        // Then
        assertThat(savedChat.getId()).isNotNull(); // 저장된 후 ID가 생성되었는지 확인
        assertThat(chatService.findById(savedChat.getId())).isPresent(); // 데이터가 저장되었는지 확인
    }

    @DisplayName("Mongodb_output")
    @Test
    public void testSaveOutputChatToMongoDB() {
        // Given
        OutputChat chat = new OutputChat();
        chat.setMessage("Hello, MongoDB!");
        chat.setTimestamp(LocalDateTime.now());

        // When
        OutputChat savedChat = outputChatService.saveChat(chat);

        // Then
        assertThat(savedChat.getId()).isNotNull(); // 저장된 후 ID가 생성되었는지 확인
        assertThat(outputChatService.findById(savedChat.getId())).isPresent(); // 데이터가 저장되었는지 확인
    }
}
