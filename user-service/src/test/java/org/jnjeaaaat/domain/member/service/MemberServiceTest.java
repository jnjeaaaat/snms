package org.jnjeaaaat.domain.member.service;

import org.jnjeaaaat.domain.member.dto.request.UpdateMemberRequest;
import org.jnjeaaaat.domain.member.dto.response.UpdateMemberResponse;
import org.jnjeaaaat.domain.member.entity.Member;
import org.jnjeaaaat.domain.member.repository.MemberRepository;
import org.jnjeaaaat.domain.member.type.MemberRole;
import org.jnjeaaaat.exception.MemberException;
import org.jnjeaaaat.global.security.CustomUserDetailsService;
import org.jnjeaaaat.global.storage.FilePathType;
import org.jnjeaaaat.global.storage.StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.jnjeaaaat.global.cons.FixedData.FIXED_TIME;
import static org.jnjeaaaat.global.cons.FixedData.TEST_IMAGE_FILE_PATH;
import static org.jnjeaaaat.global.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;

    @Mock
    CustomUserDetailsService customUserDetailsService;

    @Mock
    UserDetails userDetails;

    @Mock
    StorageService storageService;

    @InjectMocks
    MemberService memberService;

    @Nested
    @DisplayName("사용자 정보 업데이트")
    class UpdateMemberMethod {

        UpdateMemberRequest request = new UpdateMemberRequest(
                "새로운닉네임",
                "첫 자기소개 작성"
        );

        UpdateMemberResponse response = new UpdateMemberResponse(
                1L,
                "새로운닉네임",
                "https://s3.amazonaws.com/bucket/image.jpg",
                false,
                "첫 자기소개 작성",
                FIXED_TIME
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                Files.readAllBytes(Paths.get(TEST_IMAGE_FILE_PATH))
        );

        Member mockMember = createMockMember();

        UpdateMemberMethod() throws IOException {
        }

        @Test
        @DisplayName("[성공] 사용자 정보 업데이트 - 업데이트된 사용자 정보 반환")
        void success_update_user_info_when_valid_request() {
            //given
            given(memberRepository.findById(1L)).willReturn(Optional.of(mockMember));
            given(userDetails.getUsername()).willReturn("1");
            given(storageService.uploadImage(FilePathType.MEMBER, 1L, imageFile)).willReturn("profile Url");

            //when
            UpdateMemberResponse response = memberService.updateMemberInfo(
                    userDetails,
                    1L,
                    request,
                    imageFile
            );

            //then
            assertEquals("새로운닉네임", response.nickname());
            assertEquals("profile Url", response.profileImgUrl());
        }

        @Test
        @DisplayName("[실패] 사용자 정보 업데이트 - 계정 찾을 수 없음")
        void failed_update_user_info_when_NotFoundMember() {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
            //when
            //then
            assertThatThrownBy(() ->
                    memberService.updateMemberInfo(
                            userDetails,
                            1L,
                            request,
                            imageFile))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 사용자 정보 업데이트 - 삭제처리된 계정")
        void failed_update_user_info_when_AlreadyDeletedMember() {
            //given
            given(memberRepository.findById(1L)).willReturn(Optional.of(mockMember));
            mockMember.deleteMember();
            //when
            //then
            assertThatThrownBy(() ->
                    memberService.updateMemberInfo(
                            userDetails,
                            1L,
                            request,
                            imageFile))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(ALREADY_DELETED_MEMBER.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 사용자 정보 업데이트 - 요청 계정, 사용자 계정 불일치")
        void failed_update_user_info_when_AuthenticationMemberMismatch() {
            //given
            given(memberRepository.findById(1L)).willReturn(Optional.of(mockMember));
            given(userDetails.getUsername()).willReturn("2");
            //when
            //then
            assertThatThrownBy(() ->
                    memberService.updateMemberInfo(
                            userDetails,
                            1L,
                            request,
                            imageFile))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(AUTHENTICATION_MEMBER_MISMATCH.getErrorMessage());
        }
    }

    private Member createMockMember() {
        Member member = Member.builder()
                .uid("testId")
                .password("newEncodedPassword")
                .nickname("test1")
                .phoneNum("01012341234")
                .profileImageUrl("/default.jpg")
                .role(MemberRole.ROLE_USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        return member;
    }
}