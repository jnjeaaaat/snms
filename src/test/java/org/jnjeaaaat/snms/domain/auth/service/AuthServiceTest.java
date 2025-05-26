package org.jnjeaaaat.snms.domain.auth.service;

import org.jnjeaaaat.snms.domain.auth.dto.request.SignUpRequest;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.snms.domain.auth.exception.DuplicateEmailException;
import org.jnjeaaaat.snms.domain.auth.exception.UnmatchedDefaultFile;
import org.jnjeaaaat.snms.domain.auth.exception.UnmatchedPassword;
import org.jnjeaaaat.snms.domain.member.entity.Member;
import org.jnjeaaaat.snms.domain.member.repository.MemberRepository;
import org.jnjeaaaat.snms.domain.member.type.LoginType;
import org.jnjeaaaat.snms.domain.member.type.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.jnjeaaaat.snms.global.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    @Nested
    @DisplayName("로컬 회원가입 요청")
    class SignUpMethod {

        SignUpRequest request = new SignUpRequest("test@gmail.com",
                "qwER12!@", "qwER12!@", "test1", "/default.jpg");


        Member mockUser = createMockUser(
        );

        @Test
        @DisplayName("[성공] 로컬 회원가입 성공 user id 반환")
        void success_sign_up_when_valid_request() {
            //given
            given(memberRepository.existsByEmail(request.email())).willReturn(false);
            given(passwordEncoder.encode(request.password())).willReturn("newEncodedPassword");

            given(memberRepository.save(any(Member.class))).willReturn(mockUser);

            //when
            SignUpResponse response = authService.signUp(request);

            //then
            assertThat(response.id()).isNotNull();
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 중복 이메일 DuplicateEmail")
        void failed_sign_up_when_DuplicateEmail() {
            //given
            given(memberRepository.existsByEmail(request.email())).willReturn(true);

            //when
            //then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessageContaining(DUPLICATE_EMAIL.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 비밀번호 불일치 UnmatchedPassword")
        void failed_sign_up_when_UnmatchedPassword() {
            //given
            SignUpRequest request = new SignUpRequest("test@gmail.com",
                    "qwER12!@", "qwER12!!", "test1", "/default.jpg");

            //when
            //then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(UnmatchedPassword.class)
                    .hasMessageContaining(UNMATCHED_PASSWORD.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 기본 프로필 사진 불일치 UnmatchedDefaultFile")
        void failed_sign_up_when_UnmatchedDefaultFile() {
            //given
            SignUpRequest request = new SignUpRequest("test@gmail.com",
                    "qwER12!@", "qwER12!@", "test1", "/default2.jpg");

            //when
            //then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(UnmatchedDefaultFile.class)
                    .hasMessageContaining(UNMATCHED_DEFAULT_FILE.getErrorMessage());
        }
    }

    private Member createMockUser() {
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("newEncodedPassword")
                .nickname("test1")
                .profileImgUrl("/default.jpg")
                .role(MemberRole.ROLE_USER)
                .loginType(LoginType.LOCAL)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        return member;
    }

}