package org.jnjeaaaat.global.event.dto;

import lombok.Builder;

@Builder
public record FollowEventPayload(
        String followerUid,
        Long followingId
) {

    public static FollowEventPayload of(String followerUid, Long followingId) {
        return FollowEventPayload.builder()
                .followerUid(followerUid)
                .followingId(followingId)
                .build();
    }
}
