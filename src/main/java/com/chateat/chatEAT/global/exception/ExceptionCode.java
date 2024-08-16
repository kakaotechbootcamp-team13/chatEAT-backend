package com.chateat.chatEAT.global.exception;

import lombok.Getter;

public enum ExceptionCode {

    // Member
    MEMBER_ROLE_DOES_NOT_EXISTS(404, "회원의 권한이 존재하지 않습니다."),
    MEMBER_EXISTS(404, "이미 존재하는 회원입니다."),
    AUTH_CODE_IS_NOT_SAME(404, "인증 번호가 일치하지 않습니다."),
    ALREADY_EXIST_EMAIL(409, "이미 존재하는 이메일입니다."),
    ALREADY_EXIST_NICKNAME(409, "이미 존재하는 닉네임입니다."),
    NOT_EXIST_ROLE(400, "유저의 권한이 존재하지 않습니다."),
    WRONG_PASSWORD(400, "비밀번호가 올바르지 않습니다."),
    LOCKED_MEMBER(403, "비활성화된 계정입니다."),
    NOT_SOCIAL_MEMBER(404, "소셜 로그인 회원이 아닙니다."),
    WRONG_ROLE(400, "올바르지 않은 Role 입니다."),

    // Security, JWT
    NO_ACCESS_TOKEN(403, "토큰에 권한 정보가 존재하지 않습니다."),
    TOKEN_SIGNATURE_INVALID(400, "토큰 Signature가 올바르지 않습니다."),
    TOKEN_EXPIRED(400, "토큰이 만료되었습니다."),
    TOKEN_UNSUPPORTED(400, "지원하지 않는 형식의 토큰입니다."),
    TOKEN_ILLEGAL_ARGUMENT(400, "올바르지 않은 토큰입니다."),
    MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다."),
    HEADER_REFRESH_TOKEN_NOT_EXISTS(404, "헤더에 Refresh token이 존재하지 않습니다."),
    TOKEN_IS_NOT_SAME(404, "토큰이 일치하지 않습니다."),

    // AES
    ENCRYPTION_FAILED(404, "암호화에 실패했습니다."),
    DECRYPTION_FAILED(404, "복호화에 실패했습니다."),
    SECRET_KEY_INVALID(400, "Secret key가 올바르지 않습니다."),
    MEMBER_ROLE_INVALID(400, "회원의 권한 정보가 올바르지 않습니다."),

    // Redis
    NOT_FOUND_AVAILABLE_PORT(404, "사용 가능한 Port를 찾을 수 없습니다. port: 10000 ~ 65535"),
    REDIS_SERVER_EXECUTABLE_NOT_FOUND(404, "Redis Server Executable not found"),

    // Utils
    STRING_IS_NOT_LOCAL_DATE_FORMAT(404, "문자열 데이터 형식이 LocalDate 형식이 아닙니다."),
    STRING_IS_NOT_LOCAL_DATE_TIME_FORMAT(404, "문자열 데이터 형식이 LocalDateTime 형식이 아닙니다."),
    UNABLE_TO_CONVERT_LIST_TO_STRING(404, "리스트를 문자열로 변환할 수 없습니다."),
    UNABLE_TO_CONVERT_STRING_TO_LIST(404, "문자열을 리스트로 변환할 수 없습니다.");

    @Getter
    private final int status;

    @Getter
    private final String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
