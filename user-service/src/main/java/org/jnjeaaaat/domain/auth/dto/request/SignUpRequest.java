package org.jnjeaaaat.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.jnjeaaaat.domain.member.entity.Member;
import org.jnjeaaaat.domain.member.entity.MemberProvider;
import org.jnjeaaaat.domain.member.type.MemberRole;
import org.jnjeaaaat.global.validator.annotation.*;

public record SignUpRequest(

        @Trim
        @NotBlank
        @ValidUid
        String uid,

        @NotBlank
        @ValidPassword
        String password,

        @NotBlank
        @ValidPassword
        String confirmPassword,

        @Trim
        @NotBlank
        @ValidNickname
        String nickname,

        @NotBlank
        @ValidPhoneNumber
        String phoneNum,

        @NotBlank
        @ValidDefaultImg
        String profileImageUrl
) {


    public static Member toMemberEntity(SignUpRequest request, String password) {
        return Member.builder()
                .uid(request.uid())
                .password(password)
                .nickname(request.nickname())
                .phoneNum(request.phoneNum())
                .profileImageUrl(request.profileImageUrl())
                .role(MemberRole.ROLE_USER)
                .build();
    }

    public static MemberProvider toMemberProviderEntity(Member member,
                                                        String providerName,
                                                        String providerUserId,
                                                        String email) {

        return MemberProvider.builder()
                .member(member)
                .providerName(providerName)
                .providerUserId(providerUserId)
                .email(email)
                .build();
    }
}
