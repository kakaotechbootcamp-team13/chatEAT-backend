package com.chateat.chatEAT.oauth2;

import com.chateat.chatEAT.auth.principaldetails.PrincipalDetails;
import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2 로그인 요청이 들어왔습니다.");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = SocialType.KAKAO;
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, userNameAttributeName, attributes);
        Member createdMember = getMember(extractAttributes, socialType);

        return PrincipalDetails.of(createdMember, attributes);
    }

    private Member getMember(OAuth2Attributes attributes, SocialType socialType) {
        Member findMember = memberRepository.findBySocialTypeAndSocialId(socialType,
                attributes.getOAuth2MemberInfo().getId()).orElse(null);

        if (findMember == null) {
            return saveMember(attributes, socialType);
        }
        return findMember;
    }

    private Member saveMember(OAuth2Attributes oAuth2Attributes, SocialType socialType) {
        if (memberRepository.findByEmail(oAuth2Attributes.getOAuth2MemberInfo().getEmail()).isPresent()) {
            Member existingMember = memberRepository.findByEmail(oAuth2Attributes.getOAuth2MemberInfo().getEmail())
                    .get();
            SocialType existingSocialType = existingMember.getSocialType();
            throw new BadCredentialsException(
                    oAuth2Attributes.getOAuth2MemberInfo().getEmail() + "&socialType=" + existingSocialType);
        }
        Member createdMember = oAuth2Attributes.toEntity(socialType, oAuth2Attributes.getOAuth2MemberInfo());
        return memberRepository.save(createdMember);
    }
}
