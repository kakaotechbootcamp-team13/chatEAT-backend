package com.chateat.chatEAT.domain.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailCheckResponse {

    private boolean check;
    private String socialType;

    public static EmailCheckResponse of(boolean check, String socialType) {
        return new EmailCheckResponse(check, socialType);
    }
}
