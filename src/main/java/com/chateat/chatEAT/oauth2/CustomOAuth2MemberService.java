package com.chateat.chatEAT.oauth2;

import com.chateat.chatEAT.auth.principaldetails.PrincipalDetails;
import com.chateat.chatEAT.domain.member.Member;
import com.chateat.chatEAT.domain.member.repository.MemberRepository;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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
        createdMember.updatePassword(passwordEncoder, getRandomPassword(10));
        return memberRepository.save(createdMember);
    }

    private static final char[] rndAllCharacters = new char[]{
            //number
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            //uppercase
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            //lowercase
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            //special symbols
            '@', '$', '!', '%', '*', '?', '&'
    };

    private static final char[] numberCharacters = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static final char[] uppercaseCharacters = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final char[] lowercaseCharacters = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final char[] specialSymbolCharacters = new char[]{
            '@', '$', '!', '%', '*', '?', '&'
    };

    private String getRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        List<Character> passwordCharacters = new ArrayList<>();

        int numberCharactersLength = numberCharacters.length;
        passwordCharacters.add(numberCharacters[random.nextInt(numberCharactersLength)]);

        int uppercaseCharactersLength = uppercaseCharacters.length;
        passwordCharacters.add(uppercaseCharacters[random.nextInt(uppercaseCharactersLength)]);

        int lowercaseCharactersLength = lowercaseCharacters.length;
        passwordCharacters.add(lowercaseCharacters[random.nextInt(lowercaseCharactersLength)]);

        int specialSymbolCharactersLength = specialSymbolCharacters.length;
        passwordCharacters.add(specialSymbolCharacters[random.nextInt(specialSymbolCharactersLength)]);

        int rndAllCharactersLength = rndAllCharacters.length;
        for (int i = 0; i < length - 4; i++) {
            passwordCharacters.add(rndAllCharacters[random.nextInt(rndAllCharactersLength)]);
        }

        Collections.shuffle(passwordCharacters);

        for (Character character : passwordCharacters) {
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }
}
