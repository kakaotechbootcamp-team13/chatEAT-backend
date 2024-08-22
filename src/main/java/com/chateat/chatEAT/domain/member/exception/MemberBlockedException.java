package com.chateat.chatEAT.domain.member.exception;

import org.springframework.security.core.AuthenticationException;

public class MemberBlockedException extends AuthenticationException {
    public MemberBlockedException(String message) {
        super(message);
    }
}
