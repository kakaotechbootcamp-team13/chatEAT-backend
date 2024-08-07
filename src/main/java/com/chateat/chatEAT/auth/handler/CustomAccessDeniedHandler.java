package com.chateat.chatEAT.auth.handler;

import com.chateat.chatEAT.auth.utils.Responder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException {
        Responder.sendErrorResponse(httpServletResponse, HttpStatus.FORBIDDEN);
        log.warn("Forbidden Error : {}", e.getMessage());
    }
}
