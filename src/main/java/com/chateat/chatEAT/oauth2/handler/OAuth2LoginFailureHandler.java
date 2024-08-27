package com.chateat.chatEAT.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${serverUri.frontendServer}")
    private String frontendServer;

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String targetUrl = determineTargetUrl(exception);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        log.info("Fail Social Login : {}", exception.getMessage());
    }

    private String determineTargetUrl(AuthenticationException exception) {
        String errorMessage = "failedSocialLogin";
        return UriComponentsBuilder.fromUriString(frontendServer + "/error")
                .queryParam("error", errorMessage)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }
}
