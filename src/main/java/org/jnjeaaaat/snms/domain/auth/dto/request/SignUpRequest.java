package org.jnjeaaaat.snms.domain.auth.dto.request;

import org.jnjeaaaat.snms.domain.member.entity.Member;
import org.jnjeaaaat.snms.domain.member.type.LoginType;
import org.jnjeaaaat.snms.domain.member.type.MemberRole;

public record SignUpRequest(
        String email,

        String password,

        String confirmPassword,

        String nickname,

        String profileImgUrl
) {

    public static Member toEntity(SignUpRequest request, String password, LoginType loginType) {
        return Member.builder()
                .email(request.email())
                .password(password)
                .nickname(request.nickname())
                .profileImgUrl(request.profileImgUrl())
                .loginType(loginType)
                .role(MemberRole.ROLE_USER)
                .build();
    }
}
