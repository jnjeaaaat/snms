package org.jnjeaaaat.dto.member;

import lombok.Builder;

@Builder
public record MemberInfoResponse(
        Long id,
        String uid,
        String nickname,
        String profileImageUrl
) {
}
