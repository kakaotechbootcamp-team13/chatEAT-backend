package com.chateat.chatEAT.domain.member.response;

import com.chateat.chatEAT.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2JoinResponse {

    String email;
    String nickname;
    String socialType;

    public static OAuth2JoinResponse of(Member member) {
        return new OAuth2JoinResponse(member.getEmail(), member.getNickname(), String.valueOf(member.getSocialType()));
    }

}
