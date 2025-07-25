package org.jnjeaaaat.global.event.dto;

import lombok.Builder;

@Builder
public record PostCreatedEventPayload(
        Long postId,
        Long memberId,
        String memberUid
) {

    public static PostCreatedEventPayload of(Long postId, Long memberId, String memberUid) {
        return PostCreatedEventPayload.builder()
                .postId(postId)
                .memberId(memberId)
                .memberUid(memberUid)
                .build();
    }
}
