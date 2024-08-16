package com.chateat.chatEAT.oauth2;

import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.Role;
import com.chateat.chatEAT.oauth2.userinfo.KakaoOAuth2MemberInfo;
import com.chateat.chatEAT.oauth2.userinfo.OAuth2MemberInfo;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuth2Attributes {
    private final String nameAttributeKey;
    private final OAuth2MemberInfo oAuth2MemberInfo;

    @Builder
    private OAuth2Attributes(String nameAttributeKey, OAuth2MemberInfo oAuth2MemberInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2MemberInfo = oAuth2MemberInfo;
    }

    public static OAuth2Attributes of(SocialType socialType, String userNameAttributeName,
                                      Map<String, Object> attributes) {
        return ofKakao(userNameAttributeName, attributes);
    }

    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2MemberInfo(new KakaoOAuth2MemberInfo(attributes))
                .build();
    }

    public Member toEntity(SocialType socialType, OAuth2MemberInfo oAuth2MemberInfo) {
        return Member.builder()
                .socialType(socialType)
                .socialId(oAuth2MemberInfo.getId())
                .email(oAuth2MemberInfo.getEmail())
                .role(Role.GUEST)
                .build();
    }
}
