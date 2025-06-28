package org.jnjeaaaat.domain.dto.response;

import lombok.Builder;
import org.jnjeaaaat.domain.entity.Post;
import org.jnjeaaaat.dto.member.MemberInfoResponse;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostInfoResponse(
        Long id,
        MemberInfoResponse member,
        String content,
        List<PostImageResponse> postImageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PostInfoResponse fromEntity(Post post, MemberInfoResponse member) {
        return builder()
                .id(post.getId())
                .member(member)
                .content(post.getContent())
                .postImageUrls(post.getPostImages().stream()
                        .map(PostImageResponse::fromEntity)
                        .toList())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
