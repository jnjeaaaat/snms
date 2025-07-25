package org.jnjeaaaat.global.security.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.member.entity.Member;
import org.jnjeaaaat.domain.member.entity.MemberProvider;
import org.jnjeaaaat.exception.MemberException;
import org.jnjeaaaat.domain.member.repository.MemberProviderRepository;
import org.jnjeaaaat.domain.member.repository.MemberRepository;
import org.jnjeaaaat.domain.member.type.LoginType;
import org.jnjeaaaat.global.security.oauth.CustomUserDetails;
import org.jnjeaaaat.global.security.oauth.dto.OAuthUserInfo;
import org.jnjeaaaat.global.security.oauth.exception.CustomOAuth2Exception;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static org.jnjeaaaat.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.jnjeaaaat.global.exception.ErrorCode.VERIFY_PHONE_NUM;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final MemberProviderRepository memberProviderRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // todo: log 삭제
        log.info("=== OAuth2 사용자 정보 로드 시작 ===");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("사용자 정보: {}", attributes);

        String providerName = userRequest.getClientRegistration().getRegistrationId();
        OAuthUserInfo oAuthUserInfo = extractUserInfoFromOAuth2User(providerName, attributes);
        log.info("Provider: {}", providerName);

        Optional<MemberProvider> memberProvider =
                memberProviderRepository.findByProviderNameAndProviderUserId(
                        LoginType.getLoginTypeFromProviderName(providerName),
                        oAuthUserInfo.providerUserId()
                );

        if (memberProvider.isEmpty()) {
            throw new CustomOAuth2Exception(VERIFY_PHONE_NUM, providerName, oAuthUserInfo.providerUserId(), attributes);
        }

        Member member = memberRepository.findById(memberProvider.get().getMember().getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        return new CustomUserDetails(member, attributes);
    }

    private OAuthUserInfo extractUserInfoFromOAuth2User(String providerName, Map<String, Object> attributes) {
        return LoginType
                .getLoginTypeFromProviderName(providerName)
                .getUserInfo(attributes);
    }
}
