package com.chateat.chatEAT.auth.utils;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
public class CustomAuthorityUtils {
    public static List<GrantedAuthority> createAuthorities(String role) {
        log.info("Creating authorities for role {}", role);
        return List.of(new SimpleGrantedAuthority(role));
    }
}
