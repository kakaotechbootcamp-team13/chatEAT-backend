package com.chateat.chatEAT.domain.member.service;

import com.chateat.chatEAT.auth.login.GetLoginMember;
import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.exception.MemberException;
import com.chateat.chatEAT.domain.member.exception.MemberExceptionType;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.request.MemberUpdateRequest;
import com.chateat.chatEAT.domain.member.request.MemberWithdrawRequest;
import com.chateat.chatEAT.domain.member.request.UpdatePasswordRequest;
import com.chateat.chatEAT.domain.member.response.*;
import com.chateat.chatEAT.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberJoinResponse join(MemberJoinRequest memberJoinRequest) throws BaseException {
        Member member = memberJoinRequest.toEntity();
        member.authorizeUser();
        member.encodePassword(passwordEncoder);

        if (memberRepository.findByEmail(memberJoinRequest.email()).isPresent()) {
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_EMAIL);
        }

        memberRepository.save(member);
        return MemberJoinResponse.of(member);
    }

    @Override
    public MemberUpdateResponse update(MemberUpdateRequest memberUpdateRequest) throws BaseException {
        String email = GetLoginMember.getLoginMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        if (memberRepository.findByNickname(memberUpdateRequest.newNickname()).isPresent()) {
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_NICKNAME);
        }
        member.updateNickname(memberUpdateRequest.newNickname());
        return MemberUpdateResponse.of(member);
    }

    @Override
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest) throws BaseException {
        String email = GetLoginMember.getLoginMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        if (!member.matchPassword(passwordEncoder, updatePasswordRequest.beforePassword())) {
            throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        member.updatePassword(passwordEncoder, updatePasswordRequest.newPassword());
    }

    @Override
    public MemberWithdrawResponse withdraw(MemberWithdrawRequest memberWithdrawRequest) throws BaseException {
        String email = GetLoginMember.getLoginMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        if (!member.matchPassword(passwordEncoder, memberWithdrawRequest.password())) {
            throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        memberRepository.delete(member);
        return MemberWithdrawResponse.of(email);
    }

    @Override
    public MyInfoResponse myInfo() throws BaseException {
        String email = GetLoginMember.getLoginMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        return MyInfoResponse.of(member);
    }

    @Override
    public EmailCheckResponse checkEmail(String email) throws BaseException {
        boolean check = memberRepository.findByEmail(email).isPresent();
        String socialType = "NORMAL";
        if (check) {
            Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
            socialType = member.getSocialType().toString();
        }
        return EmailCheckResponse.of(check, socialType);
    }

    @Override
    public boolean checkNickname(String nickname) throws BaseException {
        return memberRepository.findByNickname(nickname).isPresent();
    }
}
