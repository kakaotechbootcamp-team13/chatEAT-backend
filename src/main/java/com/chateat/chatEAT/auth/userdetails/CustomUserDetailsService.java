package com.chateat.chatEAT.auth.userdetails;

import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.global.exception.BusinessLogicException;
import com.chateat.chatEAT.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername execute, email {}", email);
        return memberRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> {
                    log.debug("loadUserByUsername exception occur email {}", email);
                    return new BusinessLogicException(ExceptionCode.NOT_FOUND_MEMBER);
                });
    }

    private UserDetails createUserDetails(Member member) {
        return CustomUserDetails.of(member);
    }
}
