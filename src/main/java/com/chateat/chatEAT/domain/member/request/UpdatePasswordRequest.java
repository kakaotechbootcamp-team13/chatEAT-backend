package com.chateat.chatEAT.domain.member.request;

import jakarta.validation.constraints.Pattern;

public record UpdatePasswordRequest(String beforePassword,
                                    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&?])[A-Za-z\\d!@#$%^&?]{8,30}$",
                                            message = "비밀번호는 8자리 이상이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.") String newPassword) {
}
