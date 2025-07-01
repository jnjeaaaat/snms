package org.jnjeaaaat.domain.dto.response;

import lombok.Builder;
import org.jnjeaaaat.domain.entity.Post;

@Builder
public record CreatePostResponse(
        Long id,
        Long memberId
) {

    public static CreatePostResponse fromEntity(Post post) {
        return builder()
                .id(post.getId())
                .memberId(post.getMemberId())
                .build();
    }

}
