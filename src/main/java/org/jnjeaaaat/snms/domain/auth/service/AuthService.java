package org.jnjeaaaat.snms.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignInRequest;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignUpRequest;
import org.jnjeaaaat.snms.domain.auth.dto.request.SmsVerifyRequest;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignInResponse;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.snms.domain.auth.entity.RedisSms;
import org.jnjeaaaat.snms.domain.auth.entity.RedisToken;
import org.jnjeaaaat.snms.domain.auth.exception.*;
import org.jnjeaaaat.snms.domain.auth.repository.RedisSmsRepository;
import org.jnjeaaaat.snms.domain.auth.repository.RedisTokenRepository;
import org.jnjeaaaat.snms.domain.member.entity.Member;
import org.jnjeaaaat.snms.domain.member.repository.MemberRepository;
import org.jnjeaaaat.snms.global.security.CustomUserDetailsService;
import org.jnjeaaaat.snms.global.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenRepository redisTokenRepository;
    private final RedisSmsRepository redisSmsRepository;

    // 로컬 회원가입
    public SignUpResponse signUp(SignUpRequest request) {

        validateExistUid(request.uid());
        validatePassword(request.password(), request.confirmPassword());
        validateDefaultFile(request.profileImgUrl());

        Member savedMember = memberRepository.save(
                SignUpRequest.toEntity(
                        request,
                        passwordEncoder.encode(request.password())
                )
        );

        log.info("회원가입 성공 user_id : {}", savedMember.getId());

        return SignUpResponse.fromEntity(savedMember);
    }

    private void validateExistUid(String uid) {
        if (memberRepository.existsByUid(uid)) {
            throw new DuplicateUidException();
        }
    }

    private void validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new UnmatchedPassword();
        }
    }

    private void validateDefaultFile(String profileImgUrl) {
        String defaultFilename = "/default.jpg";

        if (!profileImgUrl.endsWith(defaultFilename)) {
            throw new UnmatchedDefaultFile();
        }
    }

    // 로그인
    public SignInResponse signIn(SignInRequest request) {
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
                userDetails.getUsername(),
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
            throw new WrongPassword();
        }
    }

    // 인증코드 확인
    public void verifyAuthCode(SmsVerifyRequest request) {

        RedisSms redisSms = redisSmsRepository.findById(request.phoneNum())
                .orElseThrow(NotFoundPhoneNumException::new);

        validateAuthCode(redisSms, request.authCode());

        redisSms.verify();

        redisSmsRepository.save(redisSms);
    }

    private void validateAuthCode(RedisSms redisSms, String authCode) {

        if (redisSms.isVerified()) {
            throw new AlreadyVerifiedPhoneNumException();
        }

        if (!redisSms.getAuthCode().equals(authCode)) {
            throw new WrongAuthCodeException();
        }
    }
}
