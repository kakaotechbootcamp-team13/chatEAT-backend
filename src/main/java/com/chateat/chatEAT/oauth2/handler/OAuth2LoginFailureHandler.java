package com.chateat.chatEAT.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(exception);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        log.info("Fail Social Login : {}", exception.getMessage());
    }

    private String determineTargetUrl(AuthenticationException exception) {
        String errorMessage = "failedSocialLogin";
        return UriComponentsBuilder.fromUriString("http://localhost:3000/error")
                .queryParam("error", errorMessage)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }
}
