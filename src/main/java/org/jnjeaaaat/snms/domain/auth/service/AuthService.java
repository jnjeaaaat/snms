package org.jnjeaaaat.snms.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignInRequest;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignUpRequest;
import org.jnjeaaaat.snms.domain.auth.dto.request.SmsVerifyRequest;
import org.jnjeaaaat.snms.domain.auth.dto.request.UidCheckRequest;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignInResponse;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.snms.domain.auth.dto.response.VerifyCodeResponse;
import org.jnjeaaaat.snms.domain.auth.entity.RedisSms;
import org.jnjeaaaat.snms.domain.auth.entity.RedisToken;
import org.jnjeaaaat.snms.domain.auth.exception.AuthException;
import org.jnjeaaaat.snms.domain.auth.repository.RedisSmsRepository;
import org.jnjeaaaat.snms.domain.auth.repository.RedisTokenRepository;
import org.jnjeaaaat.snms.domain.member.entity.Member;
import org.jnjeaaaat.snms.domain.member.entity.MemberProvider;
import org.jnjeaaaat.snms.domain.member.exception.MemberException;
import org.jnjeaaaat.snms.domain.member.repository.MemberProviderRepository;
import org.jnjeaaaat.snms.domain.member.repository.MemberRepository;
import org.jnjeaaaat.snms.global.security.CustomUserDetailsService;
import org.jnjeaaaat.snms.global.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberProviderRepository memberProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenRepository redisTokenRepository;
    private final RedisSmsRepository redisSmsRepository;

    @Transactional
    // 로컬 회원가입
    public SignUpResponse signUp(SignUpRequest request, String providerName, String providerUserId, String email) {

        memberRepository.findByPhoneNum(request.phoneNum())
                .ifPresent(member -> {
                    throw new AuthException(DUPLICATE_MEMBER,
                            "이미 가입된 유저 입니다. ID: " + member.getUid());
                });

        validatePassword(request.password(), request.confirmPassword());

        Member savedMember = memberRepository.save(
                SignUpRequest.toMemberEntity(
                        request,
                        passwordEncoder.encode(request.password())
                )
        );

        // OAuth 일때만 provider 저장
        if (!ObjectUtils.isEmpty(providerName)) {
            memberProviderRepository.save(
                    SignUpRequest.toMemberProviderEntity(
                            savedMember,
                            providerName,
                            providerUserId,
                            email
                    )
            );
        }

        // 회원가입 하면 로그인 처리
        String accessToken = signInAndGetAccessToken(savedMember);

        log.info("회원가입 성공 user_id : {}", savedMember.getId());

        return SignUpResponse.fromEntity(savedMember, accessToken);
    }

    // 아이디 중복 확인
    public void checkUid(UidCheckRequest request) {
        if (memberRepository.existsByUid(request.uid())) {
            throw new AuthException(DUPLICATE_UID);
        }
    }

    private void validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new AuthException(UNMATCHED_PASSWORD);
        }
    }

    // 로그인
    public SignInResponse signIn(SignInRequest request) {

        log.info("로그인 요청 uid : {}", request.uid());

        // member 확인 후 userDetails 추출
        UserDetails userDetails = getUserDetails(request.uid());

        validatePasswordMatch(request.password(), userDetails.getPassword());

        // token 생성을 위한 authentication 생성
        Authentication authentication = createAuthentication(userDetails);

        // token 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // Redis 저장
        RedisToken redisToken = new RedisToken(
                Long.valueOf(userDetails.getUsername()),
                refreshToken,
                accessToken
        );
        redisTokenRepository.save(redisToken);

        return new SignInResponse(accessToken);
    }

    private UserDetails getUserDetails(String uid) {
        return customUserDetailsService.loadUserByUsername(uid);
    }

    private Authentication createAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

    private void validatePasswordMatch(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new AuthException(WRONG_PASSWORD);
        }
    }

    // 인증코드 확인
    @Transactional
    public VerifyCodeResponse verifyAuthCode(SmsVerifyRequest request,
                                             String providerName,
                                             String providerUserId,
                                             String email) {

        RedisSms redisSms = redisSmsRepository.findById(request.phoneNum())
                .orElseThrow(() -> new AuthException(NOT_FOUND_PHONENUM));

        validateAuthCode(redisSms, request.authCode());
        redisSms.verify();
        redisSmsRepository.save(redisSms);

        // 기존에 Member 가 없을 때 회원가입 진행
        if (!memberRepository.existsByPhoneNum(request.phoneNum())) {
            return new VerifyCodeResponse(
                    request.phoneNum(),
                    null,
                    true,
                    providerName,
                    providerUserId,
                    email
            );
        }

        // 기존 Member 가 있으면 memberId로 AccessToken 생성 후 반환
        Member member = memberRepository.findByPhoneNum(request.phoneNum())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        String accessToken = signInAndGetAccessToken(member);

        if (!ObjectUtils.isEmpty(providerName)) {
            memberProviderRepository.save(
                    MemberProvider.builder()
                            .member(member)
                            .providerName(providerName)
                            .providerUserId(providerUserId)
                            .email(email)
                            .build()
            );
        }

        return new VerifyCodeResponse(
                request.phoneNum(),
                accessToken,
                false,
                providerName,
                providerUserId,
                email
        );
    }

    @Transactional
    public void signOut(UserDetails userDetails) {
        redisTokenRepository.deleteById(Long.valueOf(userDetails.getUsername()));
    }

    private String signInAndGetAccessToken(Member member) {
        UserDetails userDetails = getUserDetails(member.getUid());

        Authentication authentication = createAuthentication(userDetails);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // Redis 저장
        RedisToken redisToken = new RedisToken(
                Long.valueOf(userDetails.getUsername()),
                refreshToken,
                accessToken
        );
        redisTokenRepository.save(redisToken);

        return accessToken;
    }

    private void validateAuthCode(RedisSms redisSms, String authCode) {

        if (redisSms.isVerified()) {
            throw new AuthException(ALREADY_VERIFIED_PHONENUM);
        }

        if (!redisSms.getAuthCode().equals(authCode)) {
            throw new AuthException(WRONG_AUTH_CODE);
        }
    }
}
