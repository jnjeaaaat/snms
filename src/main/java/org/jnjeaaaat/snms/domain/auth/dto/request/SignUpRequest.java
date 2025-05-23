package org.jnjeaaaat.snms.domain.auth.dto.request;

import org.jnjeaaaat.snms.domain.user.entity.User;
import org.jnjeaaaat.snms.domain.user.type.LoginType;
import org.jnjeaaaat.snms.domain.user.type.UserRole;

public record SignUpRequest(
        String email,

        String password,

        String confirmPassword,

        String nickname,

        String profileImgUrl
) {

    public static User toEntity(SignUpRequest request, String password, LoginType loginType) {
        return User.builder()
                .email(request.email())
                .password(password)
                .nickname(request.nickname())
                .profileImgUrl(request.profileImgUrl())
                .loginType(loginType)
                .role(UserRole.ROLE_USER)
                .build();
    }
}
