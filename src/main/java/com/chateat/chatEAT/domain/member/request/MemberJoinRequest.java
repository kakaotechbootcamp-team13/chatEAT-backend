package com.chateat.chatEAT.domain.member.request;

import com.chateat.chatEAT.domain.member.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberJoinRequest(@NotBlank(message = "이메일을 입력해주세요.")
                                @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$",
                                        message = "이메일 주소 양식을 확인해주세요")
                                String email,

                                @NotBlank(message = "비밀번호를 입력해주세요.")
                                @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&?])[A-Za-z\\d!@#$%^&?]{8,30}$",
                                        message = "비밀번호는 8자리 이상이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
                                String password,

                                @NotBlank(message = "닉네임을 입력해주세요")
                                @Size(min = 2, message = "닉네임이 너무 짧습니다. 최소 2글자이어야 합니다.")
                                String nickname
) {
    public Member toEntity() {
        return Member.builder().email(email).password(password).nickname(nickname).build();
    }
}
