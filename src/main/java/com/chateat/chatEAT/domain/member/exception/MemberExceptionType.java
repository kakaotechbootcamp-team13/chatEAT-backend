package com.chateat.chatEAT.domain.member.exception;

import com.chateat.chatEAT.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MemberExceptionType implements BaseExceptionType {
    NOT_FOUND_MEMBER(600, HttpStatus.NOT_FOUND, "일치하는 회원이 존재하지 않습니다."),
    ALREADY_EXIST_EMAIL(601, HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    ALREADY_EXIST_NICKNAME(602, HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    WRONG_PASSWORD(606, HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    MemberExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
