package org.jnjeaaaat.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostsResponse(
        Long id,
        Long memberId,
        String content,
        String thumbnailUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
