package com.chateat.chatEAT.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chateat.chatEAT.auth.dto.AuthDto;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.request.MemberUpdateRequest;
import com.chateat.chatEAT.domain.member.request.MemberWithdrawRequest;
import com.chateat.chatEAT.domain.member.request.UpdatePasswordRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String getAccessToken() throws Exception {
        // 회원가입 요청
        MemberJoinRequest joinRequest = new MemberJoinRequest("test@test.com", "123abc!@#", "nickname");

        mockMvc.perform(post("/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isCreated());

        // 로그인 요청
        AuthDto.LoginDto loginRequest = new AuthDto.LoginDto("test@test.com", "123abc!@#");
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getHeader("Authorization");
        return token != null ? token.replace("Bearer ", "") : null;
    }

    @Test
    public void testJoin() throws Exception {
        MemberJoinRequest request = new MemberJoinRequest("newuser@test.com", "123abc!@#", "newnickname");

        mockMvc.perform(post("/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.nickname").value("newnickname"));
    }

    @Test
    @WithMockUser("test@test.com")
    public void testUpdate() throws Exception {
        String accessToken = getAccessToken();
        MemberUpdateRequest request = new MemberUpdateRequest("newNickname");

        mockMvc.perform(patch("/members/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newNickname").value("newNickname"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @WithMockUser("test@test.com")
    public void testUpdatePassword() throws Exception {
        String accessToken = getAccessToken();
        UpdatePasswordRequest request = new UpdatePasswordRequest("123abc!@#", "newPass!@#123");

        mockMvc.perform(patch("/members/update-password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("test@test.com")
    public void testWithdraw() throws Exception {
        String accessToken = getAccessToken();
        MemberWithdrawRequest request = new MemberWithdrawRequest("123abc!@#");

        mockMvc.perform(delete("/members/withdraw")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @WithMockUser("test@test.com")
    public void testMyInfo() throws Exception {
        String accessToken = getAccessToken();

        mockMvc.perform(get("/members/myInfo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.nickname").value("nickname"));
    }

    @Test
    @WithMockUser("test@test.com")
    public void testCheckEmail() throws Exception {
        getAccessToken();
        String email = "test@test.com";

        mockMvc.perform(get("/members/email-check/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.check").value(true))
                .andExpect(jsonPath("$.socialType").value("NORMAL"));
    }

    @Test
    @WithMockUser("test@test.com")
    public void testCheckNickname() throws Exception {
        getAccessToken();
        String nickname = "nickname";

        mockMvc.perform(get("/members/nickname-check/{nickname}", nickname)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser("test@test.com")
    public void testLogout() throws Exception {
        String accessToken = getAccessToken();

        mockMvc.perform(get("/members/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
