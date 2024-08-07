package com.chateat.chatEAT.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {
    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    @DisplayName("비밀번호 암호화 테스트")
    public void passwordEncode() throws Exception {
        String password = "1234";

        String encodedPassword = passwordEncoder.encode(password);

        assertThat(encodedPassword).startsWith("{");
        assertThat(encodedPassword).contains("{bcrypt}");
        assertThat(encodedPassword).isNotEqualTo(password);
    }

    @Test
    @DisplayName("랜덤 암호화 테스트")
    public void passwordRandomEncode() throws Exception {
        String password = "1234";

        String encodedPassword = passwordEncoder.encode(password);
        String encodedPassword2 = passwordEncoder.encode(password);

        assertThat(encodedPassword).isNotEqualTo(encodedPassword2);
    }

    @Test
    @DisplayName("비밀번호 매치 테스트")
    public void matchPassword() throws Exception {
        String password = "1234";

        String encodedPassword = passwordEncoder.encode(password);

        assertThat(passwordEncoder.matches(password, encodedPassword)).isTrue();
    }

}
