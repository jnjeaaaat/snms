package org.jnjeaaaat.domain.auth.service;

import org.jnjeaaaat.domain.auth.dto.request.SignInRequest;
import org.jnjeaaaat.domain.auth.dto.request.SignUpRequest;
import org.jnjeaaaat.domain.auth.dto.request.SmsVerifyRequest;
import org.jnjeaaaat.domain.auth.dto.request.UidCheckRequest;
import org.jnjeaaaat.domain.auth.dto.response.SignInResponse;
import org.jnjeaaaat.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.domain.auth.entity.RedisSms;
import org.jnjeaaaat.exception.AuthException;
import org.jnjeaaaat.domain.auth.repository.RedisSmsRepository;
import org.jnjeaaaat.domain.member.entity.Member;
import org.jnjeaaaat.domain.member.entity.MemberProvider;
import org.jnjeaaaat.domain.member.repository.MemberProviderRepository;
import org.jnjeaaaat.domain.member.repository.MemberRepository;
import org.jnjeaaaat.domain.member.type.MemberRole;
import org.jnjeaaaat.entity.RedisToken;
import org.jnjeaaaat.entity.repository.RedisTokenRepository;
import org.jnjeaaaat.global.security.CustomUserDetailsService;
import org.jnjeaaaat.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.jnjeaaaat.global.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberProviderRepository memberProviderRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    CustomUserDetailsService customUserDetailsService;

    @Mock
    UserDetails userDetails;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    RedisTokenRepository redisTokenRepository;

    @Mock
    RedisSmsRepository redisSmsRepository;

    @InjectMocks
    AuthService authService;

    @Nested
    @DisplayName("로컬 회원가입 요청")
    class SignUpMethod {

        SignUpRequest request = new SignUpRequest("testId",
                "qwER12!@", "qwER12!@", "test1", "01012341234", "/default.jpg");

        Member mockMember = createMockMember();
        MemberProvider mockMemberProvider = createMockMemberProvider(mockMember);
        RedisToken redisToken = new RedisToken(1L, "refreshToken", "accessToken");

        @Test
        @DisplayName("[성공] 로컬 회원가입 성공 user id 반환")
        void success_sign_up_when_valid_request() {
            //given
            given(memberRepository.findByPhoneNum(request.phoneNum())).willReturn(Optional.empty());
            given(passwordEncoder.encode(request.password())).willReturn("newEncodedPassword");
            given(memberRepository.save(any(Member.class))).willReturn(mockMember);
            given(memberProviderRepository.save(any(MemberProvider.class))).willReturn(mockMemberProvider);
            given(customUserDetailsService.loadUserByUsername(request.uid())).willReturn(userDetails);
            given(userDetails.getUsername()).willReturn(String.valueOf(1L));
            given(userDetails.getPassword()).willReturn("newEncodedPassword");
            given(jwtTokenProvider.createAccessToken(any(Authentication.class))).willReturn("accessToken");
            given(jwtTokenProvider.createRefreshToken(any(Authentication.class))).willReturn("refreshToken");
            given(redisTokenRepository.save(any(RedisToken.class))).willReturn(redisToken);

            //when
            SignUpResponse response = authService.signUp(request,
                    "google", "12345", "test@gmail.com");

            //then
            assertThat(response.id()).isNotNull();
            assertThat(response.accessToken()).isNotNull();
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 중복 멤버 DuplicateMember")
        void failed_sign_up_when_DuplicateUid() {
            //given
            given(memberRepository.findByPhoneNum(request.phoneNum())).willReturn(Optional.of(mockMember));

            //when
            //then
            assertThatThrownBy(() -> authService.signUp(request,
                    "google", "12345", "test@gmail.com"))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining("이미 가입된 유저 입니다. ID: " + mockMember.getUid());
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 비밀번호 불일치 UnmatchedPassword")
        void failed_sign_up_when_UnmatchedPassword() {
            //given
            SignUpRequest request = new SignUpRequest("testId",
                    "qwER12!@", "qwER12!!", "test1", "010-1234-1234", "/default.jpg");

            //when
            //then
            assertThatThrownBy(() -> authService.signUp(request,
                    "google", "12345", "test@gmail.com"))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(UNMATCHED_PASSWORD.getErrorMessage());
        }

    }

    @Nested
    @DisplayName("아이디 중복확인")
    class CheckUidMethod {

        UidCheckRequest request = new UidCheckRequest("testId");

        @Test
        @DisplayName("[성공] 아이디 중복확인 ok status")
        void success_check_uid_when_valid_request() {
            //given
            given(memberRepository.existsByUid(request.uid())).willReturn(false);
            //when
            //then
            assertThatCode(() -> authService.checkUid(request))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("[실패] 아아디 중복확인 - 중복 아이디 DuplicateUid")
        void failed_check_uid_when_DuplicateUid() {
            //given
            given(memberRepository.existsByUid(request.uid())).willReturn(true);
            //when
            //then
            assertThatThrownBy(() -> authService.checkUid(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(DUPLICATE_UID.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("로컬 로그인 요청")
    class SignInMethod {

        SignInRequest request = new SignInRequest("testId", "qwER12!@");
        RedisToken redisToken = new RedisToken(1L, "refreshToken", "accessToken");

        @Test
        @DisplayName("[성공] 로컬 로그인 성공 accessToken 반환")
        void success_sign_in_when_valid_request() {
            //given
            given(customUserDetailsService.loadUserByUsername(request.uid())).willReturn(userDetails);
            given(userDetails.getUsername()).willReturn(String.valueOf(1L));
            given(userDetails.getPassword()).willReturn("newEncodedPassword");
            given(passwordEncoder.matches(request.password(), userDetails.getPassword())).willReturn(true);
            given(jwtTokenProvider.createAccessToken(any(Authentication.class))).willReturn("accessToken");
            given(jwtTokenProvider.createRefreshToken(any(Authentication.class))).willReturn("refreshToken");
            given(redisTokenRepository.save(any(RedisToken.class))).willReturn(redisToken);

            //when
            SignInResponse response = authService.signIn(request);

            //then
            assertThat(response.accessToken()).isNotNull();
        }

        @Test
        @DisplayName("[실패] 로컬 로그인 - 회원 없음 NotFoundMember")
        void failed_sign_in_when_NotFoundMember() {
            //given
            given(customUserDetailsService.loadUserByUsername(request.uid())).willThrow(new AuthException(NOT_FOUND_MEMBER));
            //when
            //then
            assertThatThrownBy(() -> authService.signIn(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 로컬 로그인 - 비밀번호 인증 실패 WrongPassword")
        void failed_sign_in_when_WrongPassword() {
            //given
            given(customUserDetailsService.loadUserByUsername(request.uid())).willReturn(userDetails);
            given(passwordEncoder.matches(request.password(), userDetails.getPassword())).willReturn(false);
            //when
            //then
            assertThatThrownBy(() -> authService.signIn(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(WRONG_PASSWORD.getErrorMessage());
        }
    }

    @Test
    @DisplayName("[성공] 로그아웃")
    void test() {
        //given
        given(userDetails.getUsername()).willReturn(String.valueOf(1L));
        //when
        //then
        assertThatCode(() -> authService.signOut(userDetails))
                .doesNotThrowAnyException();
    }

    @Nested
    @DisplayName("문자인증 인증코드 확인")
    class VerifyAuthCode {

        SmsVerifyRequest request = new SmsVerifyRequest("01012341234", "123456");

        @Test
        @DisplayName("[성공] 인증코드 확인")
        void success_verify_auth_code_when_valid_request() {
            //given
            RedisSms redisSms = new RedisSms("01012341234", "123456");
            given(redisSmsRepository.findById(request.phoneNum())).willReturn(Optional.of(redisSms));
            //when
            //then
            assertThatCode(() -> authService.verifyAuthCode(request,
                    "google", "12345", "test@gmail.com"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("[실패] 인증코드 확인 - 인증코드 불일치 WRONG_AUTH_CODE")
        void failed_verify_auth_code_when_WrongAuthCode() {
            //given
            RedisSms redisSms = new RedisSms("01012341234", "111111");
            given(redisSmsRepository.findById(request.phoneNum())).willReturn(Optional.of(redisSms));
            //when
            //then
            assertThatThrownBy(() -> authService.verifyAuthCode(request, "google", "12345", "test@gmail.com"))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(WRONG_AUTH_CODE.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 인증코드 확인 - 이미 인증된 핸드폰 번호")
        void test() {
            //given
            RedisSms redisSms = new RedisSms("01012341234", "123456");
            redisSms.verify();
            given(redisSmsRepository.findById(request.phoneNum())).willReturn(Optional.of(redisSms));
            //when
            //then
            assertThatThrownBy(() -> authService.verifyAuthCode(request, "google", "12345", "test@gmail.com"))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(ALREADY_VERIFIED_PHONENUM.getErrorMessage());
        }

    }

    private Member createMockMember() {
        Member member = Member.builder()
                .uid("testId")
                .password("newEncodedPassword")
                .nickname("test1")
                .phoneNum("01012341234")
                .profileImgUrl("/default.jpg")
                .role(MemberRole.ROLE_USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        return member;
    }

    private MemberProvider createMockMemberProvider(Member member) {
        MemberProvider memberProvider = MemberProvider.builder()
                .member(member)
                .providerName("google")
                .providerUserId("12345")
                .email("test@gmail.com")
                .build();

        ReflectionTestUtils.setField(memberProvider, "id", 1L);

        return memberProvider;
    }

}