package com.chateat.chatEAT.auth.principaldetails;

import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.exception.MemberBlockedException;
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
public class PrincipalDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.debug("loadUserByUsername exception occur email {}", email);
                    return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                });

        if (member.isBlocked()) {
            log.debug("Blocked user tried to login, email {}", email);
            throw new MemberBlockedException("비활성화된 계정입니다.");
        }

        return createPrincipalDetails(member);
    }

    private UserDetails createPrincipalDetails(Member member) {
        return PrincipalDetails.of(member);
    }
}
