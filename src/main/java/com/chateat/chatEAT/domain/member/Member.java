package com.chateat.chatEAT.domain.member;

import com.chateat.chatEAT.oauth2.SocialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Table(name = "member")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    private boolean isBlocked = false;

//    private int loginFailureCount;
//
//    private static final int MAX_LOGIN_FAILURE_COUNT = 5;

    public void authorizeUser() {
        this.role = Role.USER;
    }

    public void updatePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

//    public void blockMember() {
//        this.isBlocked = true;
//    }
//
//    public void unblockMember() {
//        this.isBlocked = false;
//    }
//
//    public void increaseLoginFailureCount() {
//        this.loginFailureCount++;
//        if (this.loginFailureCount >= MAX_LOGIN_FAILURE_COUNT) {
//            this.isBlocked = true;
//        }
//    }
//
//    public void resetLoginFailureCount() {
//        this.loginFailureCount = 0;
//    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public boolean matchPassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }
}
