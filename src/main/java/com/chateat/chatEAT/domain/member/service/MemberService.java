package com.chateat.chatEAT.domain.member.service;

import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.request.MemberUpdateRequest;
import com.chateat.chatEAT.domain.member.request.MemberWithdrawRequest;
import com.chateat.chatEAT.domain.member.request.UpdatePasswordRequest;
import com.chateat.chatEAT.domain.member.response.*;
import com.chateat.chatEAT.global.exception.BaseException;

public interface MemberService {

    MemberJoinResponse join(MemberJoinRequest memberJoinRequest) throws BaseException;

    MemberUpdateResponse update(MemberUpdateRequest memberUpdateRequest) throws BaseException;

    void updatePassword(UpdatePasswordRequest updatePasswordRequest) throws BaseException;

    MemberWithdrawResponse withdraw(MemberWithdrawRequest memberWithdrawRequest) throws BaseException;

    MyInfoResponse myInfo() throws BaseException;

    EmailCheckResponse checkEmail(String email) throws BaseException;

    boolean checkNickname(String nickname) throws BaseException;
}
