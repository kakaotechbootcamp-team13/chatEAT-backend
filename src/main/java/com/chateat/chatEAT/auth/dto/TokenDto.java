package com.chateat.chatEAT.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDto {
    private final String grantType;
    private final String authorizationType;
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiresIn;
}
