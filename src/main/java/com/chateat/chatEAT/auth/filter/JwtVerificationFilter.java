package com.chateat.chatEAT.auth.filter;

import com.chateat.chatEAT.auth.jwt.JwtTokenProvider;
import com.chateat.chatEAT.global.exception.BusinessLogicException;
import com.chateat.chatEAT.global.exception.ErrorResponse;
import com.chateat.chatEAT.global.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {
    private static final List<String> EXCLUDED_PATHS =
            List.of("/", "/members/join", "/auth/login", "/auth/reissue", "/members/email-check",
                    "members/nickname-check");
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        log.info("DofilterInternal Execute");
        try {
            String accessToken = jwtTokenProvider.resolveAccessToken(request);
            if (StringUtils.hasText(accessToken) && isLogoutAccount(accessToken) && jwtTokenProvider.validateToken(
                    accessToken, response)) {
                setAuthentication(accessToken);
            }
        } catch (RuntimeException e) {
            if (e instanceof BusinessLogicException) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(
                        ErrorResponse.of(((BusinessLogicException) e).getExceptionCode()));
                response.getWriter().write(json);
                response.setStatus(((BusinessLogicException) e).getExceptionCode().getStatus());
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isLogoutAccount(String accessToken) {
        String isLogout = redisService.getValues(accessToken);
        return isLogout.equals("false");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDED_PATHS.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Set Authentication Success");
    }
}
