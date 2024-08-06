package com.chateat.chatEAT.auth.login;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class GetLoginMember {
    public static String getLoginMemberEmail() {
        Authentication loggedInMember = SecurityContextHolder.getContext().getAuthentication();
        return loggedInMember.getName();
    }
}
