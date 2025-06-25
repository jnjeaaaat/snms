package org.jnjeaaaat.domain.auth.dto.response;

import lombok.Builder;
import org.jnjeaaaat.domain.member.entity.Member;

@Builder
public record SignUpResponse(
        Long id,

        String accessToken
) {

    public static SignUpResponse fromEntity(Member member, String accessToken) {
        return builder()
                .id(member.getId())
                .accessToken(accessToken)
                .build();
    }

}
