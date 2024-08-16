package com.chateat.chatEAT.domain.member.response;

import com.chateat.chatEAT.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoResponse {

    String email;
    String nickname;
    String socialType;
    String role;

    public static MyInfoResponse of(Member member) {
        return new MyInfoResponse(member.getEmail(), member.getNickname(),
                member.getSocialType() != null ? member.getSocialType().toString() : null, member.getRole().getKey());
    }
}
