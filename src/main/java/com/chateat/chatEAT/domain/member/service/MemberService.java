package com.chateat.chatEAT.domain.member.service;

import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.request.MemberUpdateRequest;
import com.chateat.chatEAT.domain.member.request.MemberWithdrawRequest;
import com.chateat.chatEAT.domain.member.request.UpdatePasswordRequest;
import com.chateat.chatEAT.domain.member.response.EmailCheckResponse;
import com.chateat.chatEAT.domain.member.response.MemberJoinResponse;
import com.chateat.chatEAT.domain.member.response.MemberUpdateResponse;
import com.chateat.chatEAT.domain.member.response.MemberWithdrawResponse;
import com.chateat.chatEAT.domain.member.response.MyInfoResponse;

public interface MemberService {

    MemberJoinResponse join(MemberJoinRequest memberJoinRequest);

    MemberUpdateResponse update(MemberUpdateRequest memberUpdateRequest, String email);

    void updatePassword(UpdatePasswordRequest updatePasswordRequest, String email);

    MemberWithdrawResponse withdraw(MemberWithdrawRequest memberWithdrawRequest, String email);

    MyInfoResponse myInfo(String email);

    EmailCheckResponse checkEmail(String email);

    boolean checkNickname(String nickname);

    Member findMember(Long id);
}
