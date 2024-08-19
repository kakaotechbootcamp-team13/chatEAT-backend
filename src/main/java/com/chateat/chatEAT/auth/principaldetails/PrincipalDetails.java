package com.chateat.chatEAT.auth.principaldetails;

import com.chateat.chatEAT.domain.member.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class PrincipalDetails extends Member implements UserDetails, OAuth2User {
    private String email;
    private Long id;
    private String password;
    private String role;
    private Map<String, Object> attributes;

    public PrincipalDetails(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.role = member.getRole().getKey();
    }

    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this(member);
        this.attributes = attributes;
    }

    private PrincipalDetails(String email, String role) {
        this.email = email;
        this.role = role;
    }

    private PrincipalDetails(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static PrincipalDetails of(Member member) {
        return new PrincipalDetails(member);
    }

    public static PrincipalDetails of(Member member, Map<String, Object> attributes) {
        return new PrincipalDetails(member, attributes);
    }

    public static PrincipalDetails of(String email, String role) {
        return new PrincipalDetails(email, role);
    }

    public static PrincipalDetails of(String email, String password, String role) {
        return new PrincipalDetails(email, password, role);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String role() {
        return this.role;
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

    @Override
    public String getName() {
        return "";
    }
}
