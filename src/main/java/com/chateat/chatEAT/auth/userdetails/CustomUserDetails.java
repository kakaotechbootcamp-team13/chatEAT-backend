package com.chateat.chatEAT.auth.userdetails;

import com.chateat.chatEAT.auth.utils.CustomAuthorityUtils;
import com.chateat.chatEAT.domain.member.Member;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends Member implements UserDetails {
    private String email;
    private Long id;
    private String password;
    private String role;

    public CustomUserDetails(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.role = member.getRole().getKey();
    }

    private CustomUserDetails(String email, String role) {
        this.email = email;
        this.role = role;
    }

    private CustomUserDetails(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static CustomUserDetails of(Member member) {
        return new CustomUserDetails(member);
    }

    public static CustomUserDetails of(String email, String role) {
        return new CustomUserDetails(email, role);
    }

    public static CustomUserDetails of(String email, String password, String role) {
        return new CustomUserDetails(email, password, role);
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return CustomAuthorityUtils.createAuthorities(role);
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
