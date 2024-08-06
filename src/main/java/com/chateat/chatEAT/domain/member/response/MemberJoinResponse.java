package com.chateat.chatEAT.domain.member.response;

import com.chateat.chatEAT.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinResponse {

    private String email;
    private String nickname;

    public static MemberJoinResponse of(Member member) {
        return new MemberJoinResponse(member.getEmail(), member.getNickname());
    }
}
