package com.chateat.chatEAT.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String targetUrl = determineTargetUrl(exception);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        // 실패한 요청의 정보 로그 추가
        log.info("Fail Social Login: {}", exception.getMessage());
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Query String: {}", request.getQueryString());
        log.info("User Agent: {}", request.getHeader("User-Agent"));

        // 예외 전체 스택 트레이스를 로그로 출력
        log.error("Authentication failed due to: ", exception);
    }

    private String determineTargetUrl(AuthenticationException exception) {
        String errorMessage = "failedSocialLogin";
        String frontendServer = "https://www.chateat.store";
        return UriComponentsBuilder.fromUriString(frontendServer + "/error")
                .queryParam("error", errorMessage)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }
}
