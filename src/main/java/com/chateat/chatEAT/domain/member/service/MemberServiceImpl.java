package com.chateat.chatEAT.domain.member.service;

import com.chateat.chatEAT.auth.GetLoginMember;
import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.request.MemberUpdateRequest;
import com.chateat.chatEAT.domain.member.request.MemberWithdrawRequest;
import com.chateat.chatEAT.domain.member.request.UpdatePasswordRequest;
import com.chateat.chatEAT.domain.member.response.EmailCheckResponse;
import com.chateat.chatEAT.domain.member.response.MemberJoinResponse;
import com.chateat.chatEAT.domain.member.response.MemberUpdateResponse;
import com.chateat.chatEAT.domain.member.response.MemberWithdrawResponse;
import com.chateat.chatEAT.domain.member.response.MyInfoResponse;
import com.chateat.chatEAT.global.exception.BusinessLogicException;
import com.chateat.chatEAT.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberJoinResponse join(MemberJoinRequest memberJoinRequest) {
        Member member = memberJoinRequest.toEntity();
        member.authorizeUser();
        member.encodePassword(passwordEncoder);

        if (memberRepository.findByEmail(memberJoinRequest.email()).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXIST_EMAIL);
        }

        memberRepository.save(member);
        return MemberJoinResponse.of(member);
    }

    @Override
    public MemberUpdateResponse update(MemberUpdateRequest memberUpdateRequest) {
        String email = GetLoginMember.getLoginMemberEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        if (memberRepository.findByNickname(memberUpdateRequest.newNickname()).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXIST_NICKNAME);
        }
        member.updateNickname(memberUpdateRequest.newNickname());
        return MemberUpdateResponse.of(member);
    }

    @Override
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        String email = GetLoginMember.getLoginMemberEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!member.matchPassword(passwordEncoder, updatePasswordRequest.beforePassword())) {
            throw new BusinessLogicException(ExceptionCode.WRONG_PASSWORD);
        }

        member.updatePassword(passwordEncoder, updatePasswordRequest.newPassword());
    }

    @Override
    public MemberWithdrawResponse withdraw(MemberWithdrawRequest memberWithdrawRequest) {
        String email = GetLoginMember.getLoginMemberEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!member.matchPassword(passwordEncoder, memberWithdrawRequest.password())) {
            throw new BusinessLogicException(ExceptionCode.WRONG_PASSWORD);
        }

        memberRepository.delete(member);
        return MemberWithdrawResponse.of(email);
    }

    @Override
    public MyInfoResponse myInfo() {
        String email = GetLoginMember.getLoginMemberEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return MyInfoResponse.of(member);
    }

    @Override
    public EmailCheckResponse checkEmail(String email) {
        boolean check = memberRepository.findByEmail(email).isPresent();
        String socialType = "NORMAL";
        if (check) {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
            socialType = member.getSocialType().toString();
        }
        return EmailCheckResponse.of(check, socialType);
    }

    @Override
    public boolean checkNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    @Transactional(readOnly = true)
    public Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.debug("findMember By MemberService exception occur id : {}", id);
                    return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                });
    }
}
