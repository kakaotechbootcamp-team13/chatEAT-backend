package com.chateat.chatEAT.auth.handler;

import com.chateat.chatEAT.auth.utils.Responder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Exception exception = (Exception) request.getAttribute("exception");
        Responder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);
        String errorMessage = exception != null ? exception.getMessage() : authException.getMessage();
        log.warn("Unauthorized error: {}", errorMessage);
    }
}
