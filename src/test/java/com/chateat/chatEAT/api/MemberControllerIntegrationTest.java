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
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    @BeforeEach
    public void signUp() throws Exception {
        // 회원가입 요청
        MemberJoinRequest joinRequest = new MemberJoinRequest("test@test.com", "123abc!@#", "nickname");

        mockMvc.perform(post("/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isCreated());

    }

    @AfterEach
    public void deleteUser() {
        memberRepository.deleteAll();
    }

    private Map<String, String> getToken() throws Exception {
        AuthDto.LoginDto loginRequest = new AuthDto.LoginDto("test@test.com", "123abc!@#");
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, String> token = new HashMap<>();
        String accessToken = result.getResponse().getHeader("Authorization");
        String encryptedRefreshToken = result.getResponse().getHeader("Refresh");
        token.put("access_token", accessToken != null ? accessToken.replace("Bearer ", "") : null);
        token.put("refresh_token", encryptedRefreshToken);
        return token;
    }


    @Order(1)
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

    @Order(2)
    @Test
    @WithMockUser("test@test.com")
    public void testUpdate() throws Exception {
        Map<String, String> token = getToken();
        String accessToken = token.get("access_token");
        MemberUpdateRequest request = new MemberUpdateRequest("newNickname");

        mockMvc.perform(patch("/members/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newNickname").value("newNickname"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Order(3)
    @Test
    @WithMockUser("test@test.com")
    public void testUpdatePassword() throws Exception {
        Map<String, String> token = getToken();
        String accessToken = token.get("access_token");
        UpdatePasswordRequest request = new UpdatePasswordRequest("123abc!@#", "newPass!@#123");

        mockMvc.perform(patch("/members/update-password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Order(4)
    @Test
    @WithMockUser("test@test.com")
    public void testWithdraw() throws Exception {
        Map<String, String> token = getToken();
        String accessToken = token.get("access_token");
        MemberWithdrawRequest request = new MemberWithdrawRequest("123abc!@#");

        mockMvc.perform(delete("/members/withdraw")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Order(5)
    @Test
    @WithMockUser("test@test.com")
    public void testMyInfo() throws Exception {
        Map<String, String> token = getToken();
        String accessToken = token.get("access_token");

        mockMvc.perform(get("/members/myInfo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.nickname").value("nickname"));
    }

    @Order(6)
    @Test
    @WithMockUser("test@test.com")
    public void testCheckEmail() throws Exception {
        String email = "test@test.com";

        mockMvc.perform(get("/members/email-check/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.check").value(true))
                .andExpect(jsonPath("$.socialType").value("NORMAL"));
    }

    @Order(7)
    @Test
    @WithMockUser("test@test.com")
    public void testCheckNickname() throws Exception {
        String nickname = "nickname";

        mockMvc.perform(get("/members/nickname-check/{nickname}", nickname)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Order(8)
    @Test
    @WithMockUser("test@test.com")
    public void testLogout() throws Exception {
        Map<String, String> token = getToken();
        String accessToken = token.get("access_token");
        String encryptedRefreshToken = token.get("refresh_token");

        mockMvc.perform(patch("/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .header("Refresh", encryptedRefreshToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
