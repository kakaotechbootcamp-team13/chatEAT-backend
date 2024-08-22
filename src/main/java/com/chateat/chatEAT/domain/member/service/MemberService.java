package com.chateat.chatEAT.domain.member.service;

import com.chateat.chatEAT.domain.member.Member;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    MemberJoinResponse join(MemberJoinRequest memberJoinRequest);

    MemberUpdateResponse update(MemberUpdateRequest memberUpdateRequest, String email);

    void updatePassword(UpdatePasswordRequest updatePasswordRequest, String email);

    MemberWithdrawResponse withdraw(MemberWithdrawRequest memberWithdrawRequest, String email);

    void deleteMember(Long id, String email);

    MyInfoResponse myInfo(String email);

    EmailCheckResponse checkEmail(String email);

    boolean checkNickname(String nickname);

    void memberBlock(Long id);

    void memberUnblock(Long id);

    OAuth2JoinResponse oauth2Join(OAuth2JoinRequest request);

    MemberWithdrawResponse oauth2Withdraw(String email);

    AuthorizeRoleResponse changRole(AuthorizeRoleRequest request, Long id);

    Page<MemberListPageResponse> findAllMembers(Pageable pageable);

    Member findMember(Long id);
}
