package com.chateat.chatEAT.oauth2.handler;

import com.chateat.chatEAT.auth.dto.TokenDto;
import com.chateat.chatEAT.auth.jwt.JwtTokenProvider;
import com.chateat.chatEAT.auth.principaldetails.PrincipalDetails;
import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.Role;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.global.exception.BusinessLogicException;
import com.chateat.chatEAT.global.exception.ExceptionCode;
import com.chateat.chatEAT.global.redis.RedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${serverUri.frontendServer}")
    private String frontendServer;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 로그인이 성공하였습니다. Handler 적용");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String role = principalDetails.role();

        if (Objects.equals(role, Role.GUEST.getKey())) {
            handleGuestLogin(request, response, principalDetails);
        } else {
            handleUserLogin(request, response, principalDetails);
        }
    }

    private void handleGuestLogin(HttpServletRequest request, HttpServletResponse response,
                                  PrincipalDetails principalDetails) throws IOException {
        log.info("New Social Login Member");
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(principalDetails);
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        Member member = memberRepository.findByEmail(principalDetails.getUsername())
                .orElseThrow(() -> new BusinessLogicException(
                        ExceptionCode.MEMBER_NOT_FOUND));

        long refreshTokenExpirationPeriod = jwtTokenProvider.getRefreshTokenExpirationPeriod();
        redisService.setValues(member.getEmail(), refreshToken, Duration.ofMillis(refreshTokenExpirationPeriod));

        String targetUrl = UriComponentsBuilder.fromUriString(frontendServer + "/members/oauth2/join")
                .queryParam("email", principalDetails.getUsername())
                .queryParam("role", principalDetails.role())
                .queryParam("accessToken", BEARER_PREFIX + accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void handleUserLogin(HttpServletRequest request, HttpServletResponse response,
                                 PrincipalDetails principalDetails) throws IOException {
        log.info("Existing Social Login Member");
        Member member = memberRepository.findByEmail(principalDetails.getUsername())
                .orElseThrow(() -> new BusinessLogicException(
                        ExceptionCode.MEMBER_NOT_FOUND));
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(principalDetails);
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        long refreshTokenExpirationPeriod = jwtTokenProvider.getRefreshTokenExpirationPeriod();
        redisService.setValues(member.getEmail(), refreshToken, Duration.ofMillis(refreshTokenExpirationPeriod));

        String targetUrl = UriComponentsBuilder.fromUriString(frontendServer + "/login")
                .queryParam("email", member.getEmail())
                .queryParam("nickname", member.getNickname())
                .queryParam("accessToken", BEARER_PREFIX + accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
