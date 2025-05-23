package org.jnjeaaaat.snms.domain.auth.dto.response;

import lombok.Builder;
import org.jnjeaaaat.snms.domain.user.entity.User;

@Builder
public record SignUpResponse(
        Long id
) {

    public static SignUpResponse fromEntity(User user) {
        return SignUpResponse.builder()
                .id(user.getId())
                .build();
    }

}
