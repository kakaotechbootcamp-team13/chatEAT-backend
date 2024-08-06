package com.chateat.chatEAT.api;

import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.exception.MemberException;
import com.chateat.chatEAT.domain.member.exception.MemberExceptionType;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    EntityManager em;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    private final String SIGN_UP_URL = "/member/join";
    private final String signEmail = "1234@1234.com";
    private final String password = "123abc@!#";
    private final String nickname = "cat";

    private void clear() {
        em.flush();
        em.clear();
    }

    private void signUpSuccess(String data) throws Exception {
        mockMvc.perform(
                        post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(data))
                .andExpect(status().isCreated());
    }

    private void signUpFail(String data) throws Exception {
        mockMvc.perform(
                        post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(data))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 정상 테스트")
    public void signUpSucceedWithEmail() throws Exception {
        //given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(signEmail, password, nickname);
        String data = objectMapper.writeValueAsString(memberJoinRequest);

        //when
        signUpSuccess(data);

        //then
        Member member = memberRepository.findByEmail(signEmail).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        assertThat(member.getEmail()).isEqualTo(signEmail);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }
}
