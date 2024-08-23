package com.chateat.chatEAT;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(info = @Info(title = "ChatEAT API", version = "1.0", description = "API Documentation"))
public class ChatEatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatEatApplication.class, args);
    }

}
