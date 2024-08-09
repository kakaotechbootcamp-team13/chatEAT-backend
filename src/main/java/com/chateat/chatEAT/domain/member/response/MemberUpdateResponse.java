package com.chateat.chatEAT.domain.member.response;

import com.chateat.chatEAT.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateResponse {

    String email;
    String newNickname;

    public static MemberUpdateResponse of(Member member) {
        return new MemberUpdateResponse(member.getEmail(), member.getNickname());
    }
}
