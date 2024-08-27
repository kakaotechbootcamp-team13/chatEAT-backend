package com.chateat.chatEAT.auth.filter;

import com.chateat.chatEAT.auth.dto.AuthDto.LoginDto;
import com.chateat.chatEAT.auth.dto.TokenDto;
import com.chateat.chatEAT.auth.jwt.JwtTokenProvider;
import com.chateat.chatEAT.auth.principaldetails.PrincipalDetails;
import com.chateat.chatEAT.auth.utils.Responder;
import com.chateat.chatEAT.config.AES128Config;
import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.service.MemberService;
import com.chateat.chatEAT.global.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AES128Config aes128Config;
    private final MemberService memberService;
    private final RedisService redisService;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);
            log.info("Attempting authentication for email: {}", loginDto.getEmail());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(), loginDto.getPassword());
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            log.error("Error reading login request: {}", e.getMessage());
            throw new AuthenticationException("Error reading login request", e) {
            };
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("Successful authentication for email: {}", principalDetails.getUsername());
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(principalDetails);
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();
        String encryptedRefreshToken = aes128Config.encryptAes(refreshToken);

        jwtTokenProvider.accessTokenSetHeader(accessToken, response);
//        jwtTokenProvider.refreshTokenSetHeader(encryptedRefreshToken, response);
        jwtTokenProvider.refreshTokenSetCookie(encryptedRefreshToken, response);

        Member member = memberService.findMember(principalDetails.getId());
        log.info("Find member: {}", member.getEmail());

        Responder.loginSuccessResponse(response, member);

        long refreshTokenExpirationPeriod = jwtTokenProvider.getRefreshTokenExpirationPeriod();
        redisService.setValues(member.getEmail(), refreshToken, Duration.ofMillis(refreshTokenExpirationPeriod));

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authentication);
    }
}
