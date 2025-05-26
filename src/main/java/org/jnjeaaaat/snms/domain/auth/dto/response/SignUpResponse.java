package org.jnjeaaaat.snms.domain.auth.dto.response;

import lombok.Builder;
import org.jnjeaaaat.snms.domain.member.entity.Member;

@Builder
public record SignUpResponse(
        Long id
) {

    public static SignUpResponse fromEntity(Member member) {
        return SignUpResponse.builder()
                .id(member.getId())
                .build();
    }

}
