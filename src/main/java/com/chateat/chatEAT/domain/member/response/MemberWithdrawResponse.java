package com.chateat.chatEAT.domain.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberWithdrawResponse {
    
    String email;

    public static MemberWithdrawResponse of(String email) {
        return new MemberWithdrawResponse(email);
    }
}
