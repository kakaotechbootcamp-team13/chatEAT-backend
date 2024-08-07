package com.chateat.chatEAT.auth.utils;

import com.chateat.chatEAT.auth.dto.AuthDto.LoginResponse;
import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.global.dto.SingleResponseDto;
import com.chateat.chatEAT.global.exception.BusinessLogicException;
import com.chateat.chatEAT.global.exception.ErrorResponse;
import com.chateat.chatEAT.global.exception.ExceptionCode;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class Responder {
    public static void sendErrorResponse(HttpServletResponse response, HttpStatus status) throws IOException {
        Gson gson = new Gson();
        ErrorResponse errorResponse = ErrorResponse.of(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write(gson.toJson(errorResponse, ErrorResponse.class));
    }

    public static void sendErrorResponse(HttpServletResponse response, ExceptionCode exceptionCode) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        throw new BusinessLogicException(exceptionCode);
    }

    public static void loginSuccessResponse(HttpServletResponse response, Member member) throws IOException {
        Gson gson = new Gson();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        LoginResponse lgoinResponse = LoginResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole().getKey())
                .build();

        response.getWriter().write(gson.toJson(new SingleResponseDto<>(lgoinResponse), SingleResponseDto.class));
    }
}
