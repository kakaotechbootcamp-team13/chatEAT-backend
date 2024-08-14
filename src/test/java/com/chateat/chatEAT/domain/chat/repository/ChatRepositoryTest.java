package com.chateat.chatEAT.domain.chat.repository;

import com.chateat.chatEAT.domain.chat.Chat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    private ChatRepository chatRepository;

    @DisplayName("Mongodb 상호작용에 성공한다.")
    @Test
    public void testSaveChatToMongoDB() {
        // Given
        Chat chat = new Chat();
        chat.setMessage("Hello, MongoDB!");
        chat.setTimestamp(LocalDateTime.now());

        // When
        Chat savedChat = chatRepository.save(chat);

        // Then
        assertThat(savedChat.getId()).isNotNull(); // 저장된 후 ID가 생성되었는지 확인
        assertThat(chatRepository.findById(savedChat.getId())).isPresent(); // 데이터가 저장되었는지 확인
    }
}
