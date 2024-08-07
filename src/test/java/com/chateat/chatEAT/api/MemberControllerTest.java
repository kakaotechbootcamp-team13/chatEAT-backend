package com.chateat.chatEAT.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.service.MemberService;
import com.chateat.chatEAT.global.exception.BusinessLogicException;
import com.chateat.chatEAT.global.exception.ExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";
    private final String SIGN_UP_URL = "/members/join";
    private final String signEmail = "1234@1234.com";
    private final String password = "123abc@!#";
    private final String nickname = "cat";
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

    private String getAccessTokenAndLogin(String email) throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);

        MvcResult result = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(AUTHORIZATION_HEADER);
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
        Member member = memberRepository.findByEmail(signEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        assertThat(member.getEmail()).isEqualTo(signEmail);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원가입 실패 (이메일, 패스워드, 닉네임 하나가 없음)")
    public void noDataSignUp() throws Exception {
        String noEmail = objectMapper.writeValueAsString(new MemberJoinRequest(null, password, nickname));
        String noPassword = objectMapper.writeValueAsString(new MemberJoinRequest(signEmail, null, nickname));
        String noNickname = objectMapper.writeValueAsString(new MemberJoinRequest(signEmail, password, null));

        signUpFail(noEmail);
        signUpFail(noPassword);
        signUpFail(noNickname);

        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("회원정보 수정")
    public void updateMember() throws Exception {
        //given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(signEmail, password, nickname);
        String data = objectMapper.writeValueAsString(memberJoinRequest);
        signUpSuccess(data);

        String accessToken = getAccessTokenAndLogin(signEmail);

        Map<String, Object> map = new HashMap<>();
        map.put("nickname", nickname + "tiger");
        String updateData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/members/update")
                                .header(AUTHORIZATION_HEADER, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByEmail(signEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        assertThat(member.getNickname()).isEqualTo(nickname + "tiger");
    }

    @Test
    @DisplayName("회원정보 수정 (닉네임 중복 오류)")
    public void updateMemberFail() throws Exception {
        //given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(signEmail, password, nickname);
        MemberJoinRequest memberJoinRequest2 = new MemberJoinRequest("123" + signEmail, password, "tiger");
        String data = objectMapper.writeValueAsString(memberJoinRequest);
        String data2 = objectMapper.writeValueAsString(memberJoinRequest2);
        signUpSuccess(data);
        signUpSuccess(data2);

        String accessToken = getAccessTokenAndLogin(signEmail);

        Map<String, Object> map = new HashMap<>();
        map.put("nickname", "tiger");
        String updateData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/members/update")
                                .header(AUTHORIZATION_HEADER, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByEmail(signEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        assertThat(member.getNickname()).isEqualTo(nickname + "tiger");
    }

}
