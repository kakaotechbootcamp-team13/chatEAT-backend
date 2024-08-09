package com.chateat.chatEAT.auth.service;

import com.chateat.chatEAT.auth.dto.TokenDto;
import com.chateat.chatEAT.auth.jwt.JwtTokenProvider;
import com.chateat.chatEAT.auth.userdetails.CustomUserDetails;
import com.chateat.chatEAT.config.AES128Config;
import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.global.exception.BusinessLogicException;
import com.chateat.chatEAT.global.exception.ExceptionCode;
import com.chateat.chatEAT.global.redis.RedisService;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AES128Config aes128Config;
    private final RedisService redisService;
    private final MemberRepository memberRepository;

    public String reissueAccessToken(String encryptedRefreshToken) {
        this.verifiedRefreshToken(encryptedRefreshToken);
        String refreshToken = aes128Config.decryptAes(encryptedRefreshToken);
        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        String email = claims.getSubject();
        String redisRefreshToken = redisService.getValues(email);

        if (redisService.checkExistsValue(redisRefreshToken) && refreshToken.equals(redisRefreshToken)) {
            Member findMember = this.findMemberByEmail(email);
            CustomUserDetails userDetails = CustomUserDetails.of(findMember);
            TokenDto tokenDto = jwtTokenProvider.generateTokenDto(userDetails);
            String newAccessToken = tokenDto.getAccessToken();
            long refreshTokenExpirationMillis = jwtTokenProvider.getRefreshTokenExpirationPeriod();
            redisService.setValues(refreshToken, newAccessToken,
                    Duration.ofMillis(refreshTokenExpirationMillis));
            return newAccessToken;
        } else {
            log.debug("ReissueAccessToken exception occur redisRefreshToken: {}", redisRefreshToken);
            throw new BusinessLogicException(ExceptionCode.TOKEN_IS_NOT_SAME);
        }
    }

    public void logout(String encryptedRefreshToken, String accessToken) {
        this.verifiedRefreshToken(encryptedRefreshToken);
        String refreshToken = aes128Config.decryptAes(encryptedRefreshToken);
        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        String email = claims.getSubject();
        String redisRefreshToken = redisService.getValues(email);
        if (redisService.checkExistsValue(redisRefreshToken)) {
            redisService.deleteValues(email);

            // 로그아웃 시 Access Token Redis 저장 ( key = Access Token / value = "logout" ) 로그아웃 후 해당 액세스 토큰 사용 불가
            long accessTokenExpirationMillis = jwtTokenProvider.getAccessTokenExpirationPeriod();
            redisService.setValues(accessToken, "logout", Duration.ofMillis(accessTokenExpirationMillis));
        }
    }

    private void verifiedRefreshToken(String encryptedRefreshToken) {
        if (encryptedRefreshToken == null) {
            throw new BusinessLogicException(ExceptionCode.HEADER_REFRESH_TOKEN_NOT_EXISTS);
        }
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
