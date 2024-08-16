package com.chateat.chatEAT.domain.member.service;

import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.request.*;
import com.chateat.chatEAT.domain.member.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    MemberJoinResponse join(MemberJoinRequest memberJoinRequest);

    MemberUpdateResponse update(MemberUpdateRequest memberUpdateRequest, String email);

    void updatePassword(UpdatePasswordRequest updatePasswordRequest, String email);

    MemberWithdrawResponse withdraw(MemberWithdrawRequest memberWithdrawRequest, String email);

    MyInfoResponse myInfo(String email);

    EmailCheckResponse checkEmail(String email);

    boolean checkNickname(String nickname);

//    void memberBlock(String email);
//
//    void memberUnblock(String email);

    OAuth2JoinResponse oauth2Join(OAuth2JoinRequest request);

    MemberWithdrawResponse oauth2Withdraw(String email);

    AuthorizeRoleResponse changRole(AuthorizeRoleRequest request, Long id);

    Page<MemberListPageResponse> findAllMembers(Pageable pageable);

    Member findMember(Long id);
}
