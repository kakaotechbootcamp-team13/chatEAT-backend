package com.chateat.chatEAT.domain.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizeRoleResponse {
    private String email;
    private String role;

    public static AuthorizeRoleResponse of(String email, String role) {
        return new AuthorizeRoleResponse(email, role);
    }
}
