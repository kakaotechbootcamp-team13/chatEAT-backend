package com.chateat.chatEAT.domain.member.exception;

import com.chateat.chatEAT.global.exception.BaseException;
import com.chateat.chatEAT.global.exception.BaseExceptionType;

public class MemberException extends BaseException {
    private final BaseExceptionType exceptionType;

    public MemberException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
