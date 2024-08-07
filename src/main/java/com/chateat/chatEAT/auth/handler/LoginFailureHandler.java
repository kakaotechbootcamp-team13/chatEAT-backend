package com.chateat.chatEAT.auth.handler;

import com.chateat.chatEAT.auth.utils.Responder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class LoginFailureHandler implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginFailureHandler.class);

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.error("Authentication failure : {}", exception.getMessage());
        Responder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);
    }
}
