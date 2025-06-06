package org.jnjeaaaat.snms.domain.auth.dto.response;

import lombok.Builder;
import org.jnjeaaaat.snms.domain.member.entity.Member;

@Builder
public record SignUpResponse(
        Long id,

        String accessToken
) {

    public static SignUpResponse fromEntity(Member member, String accessToken) {
        return SignUpResponse.builder()
                .id(member.getId())
                .accessToken(accessToken)
                .build();
    }

}
