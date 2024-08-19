package com.chateat.chatEAT.domain.member.service;

import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.Role;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import com.chateat.chatEAT.domain.member.request.AuthorizeRoleRequest;
import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.request.MemberUpdateRequest;
import com.chateat.chatEAT.domain.member.request.MemberWithdrawRequest;
import com.chateat.chatEAT.domain.member.request.OAuth2JoinRequest;
import com.chateat.chatEAT.domain.member.request.UpdatePasswordRequest;
import com.chateat.chatEAT.domain.member.response.AuthorizeRoleResponse;
import com.chateat.chatEAT.domain.member.response.EmailCheckResponse;
import com.chateat.chatEAT.domain.member.response.MemberJoinResponse;
import com.chateat.chatEAT.domain.member.response.MemberListPageResponse;
import com.chateat.chatEAT.domain.member.response.MemberUpdateResponse;
import com.chateat.chatEAT.domain.member.response.MemberWithdrawResponse;
import com.chateat.chatEAT.domain.member.response.MyInfoResponse;
import com.chateat.chatEAT.domain.member.response.OAuth2JoinResponse;
import com.chateat.chatEAT.global.exception.BusinessLogicException;
import com.chateat.chatEAT.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        if (memberRepository.findByNickname(memberJoinRequest.nickname()).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXIST_NICKNAME);
        }

        memberRepository.save(member);
        return MemberJoinResponse.of(member);
    }

    @Override
    public MemberUpdateResponse update(MemberUpdateRequest memberUpdateRequest, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        if (memberRepository.findByNickname(memberUpdateRequest.newNickname()).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXIST_NICKNAME);
        }
        member.updateNickname(memberUpdateRequest.newNickname());
        return MemberUpdateResponse.of(member);
    }

    @Override
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!member.matchPassword(passwordEncoder, updatePasswordRequest.beforePassword())) {
            throw new BusinessLogicException(ExceptionCode.WRONG_PASSWORD);
        }

        member.updatePassword(passwordEncoder, updatePasswordRequest.newPassword());
    }

    @Override
    public MemberWithdrawResponse withdraw(MemberWithdrawRequest memberWithdrawRequest, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!member.matchPassword(passwordEncoder, memberWithdrawRequest.password())) {
            throw new BusinessLogicException(ExceptionCode.WRONG_PASSWORD);
        }

        memberRepository.delete(member);
        return MemberWithdrawResponse.of(email);
    }

    @Override
    public void deleteMember(Long id, String loginEmail) {
        Member member = memberRepository.findByEmail(loginEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        if (member.getRole() != Role.ADMIN) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_ROLE_INVALID);
        }
        Member deleteMember = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        memberRepository.delete(deleteMember);
    }

    @Override
    public MyInfoResponse myInfo(String email) {
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
            if (member.getSocialType() != null) {
                socialType = member.getSocialType().toString();
            }
        }
        return EmailCheckResponse.of(check, socialType);
    }

    @Override
    public boolean checkNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    @Override
    public OAuth2JoinResponse oauth2Join(OAuth2JoinRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        if (memberRepository.findByNickname(request.nickname()).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXIST_NICKNAME);
        }
        member.authorizeUser();
        member.updateNickname(request.nickname());
        return OAuth2JoinResponse.of(member);
    }

    @Override
    public MemberWithdrawResponse oauth2Withdraw(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        if (member.getSocialType() == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_SOCIAL_MEMBER);
        }
        memberRepository.delete(member);
        return MemberWithdrawResponse.of(email);
    }

    @Override
    public AuthorizeRoleResponse changRole(AuthorizeRoleRequest request, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        switch (request.role()) {
            case "ADMIN" -> member.authorizeAdmin();
            case "USER" -> member.authorizeUser();
            default -> throw new BusinessLogicException(ExceptionCode.WRONG_ROLE);
        }
        return AuthorizeRoleResponse.of(member.getEmail(), String.valueOf(member.getRole()));
    }

    @Override
    public Page<MemberListPageResponse> findAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberListPageResponse::of);
    }

//    @Override
//    public void memberBlock(String email) {
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
//        member.blockMember();
//    }
//
//    @Override
//    public void memberUnblock(String email) {
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
//        member.unblockMember();
//    }

    @Transactional(readOnly = true)
    public Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.debug("findMember By MemberService exception occur id : {}", id);
                    return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                });
    }
}
