package org.jnjeaaaat.snms.domain.auth.dto.request;

import org.jnjeaaaat.snms.domain.member.entity.Member;
import org.jnjeaaaat.snms.domain.member.type.MemberRole;

public record SignUpRequest(
        String uid,

        String password,

        String confirmPassword,

        String nickname,

        String phoneNum,

        String profileImgUrl
) {

    public static Member toEntity(SignUpRequest request, String password) {
        return Member.builder()
                .uid(request.uid())
                .password(password)
                .nickname(request.nickname())
                .phoneNum(request.phoneNum())
                .profileImgUrl(request.profileImgUrl())
                .role(MemberRole.ROLE_USER)
                .build();
    }
}
