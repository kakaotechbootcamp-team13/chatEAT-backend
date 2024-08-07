package com.chateat.chatEAT.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) {
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        String email = userDetails.getEmail();
//        memberRepository.findByEmail(email)
//                .ifPresent(member -> {
//                    if (member.isBlocked()) {
//                        throw new BusinessLogicException(ExceptionCode.LOCKED_MEMBER);
//                    }
//                });
        Collection<? extends GrantedAuthority> authoritiesCollection = authentication.getAuthorities();
        log.info("Authentication successful (By Success Handler)");
        log.info("ID : {}", authentication.getName());
        log.info("Roles : {}", authoritiesCollection.toString());
    }
}

