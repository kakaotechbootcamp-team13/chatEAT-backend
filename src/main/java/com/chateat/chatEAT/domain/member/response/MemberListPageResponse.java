package com.chateat.chatEAT.domain.member.response;

import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.Role;
import com.chateat.chatEAT.oauth2.SocialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberListPageResponse {
    private Long id;
    private String email;
    private String nickname;
    private Role role;
    private SocialType socialType;

    public static MemberListPageResponse of(Member member) {
        return new MemberListPageResponse(member.getId(), member.getEmail(), member.getNickname(), member.getRole(), member.getSocialType());
    }
}
