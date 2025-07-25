package org.jnjeaaaat.dto.member;

import lombok.Builder;

@Builder
public record FollowerInfoResponse(
        Long followerId,
        String uid,
        String profileImageUrl
) {

}
