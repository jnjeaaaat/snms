package org.jnjeaaaat.domain.dto;

import lombok.Builder;
import org.jnjeaaaat.domain.entity.Post;
import org.jnjeaaaat.domain.entity.PostImages;

import java.util.List;

@Builder
public record CreatePostResponse(
        Long id,
        Long memberId,
        String content,
        List<PostImagesResponse> postImageUrls
) {

    @Builder
    private record PostImagesResponse(
            String imageUrl,
            Boolean isThumbnail
    ) {

        private static PostImagesResponse fromImageEntity(PostImages postImages) {
            return PostImagesResponse.builder()
                    .imageUrl(postImages.getImageUrl())
                    .isThumbnail(postImages.getIsThumbnail())
                    .build();
        }

    }

    public static CreatePostResponse fromEntity(Post post) {
        return builder()
                .id(post.getId())
                .memberId(post.getMemberId())
                .content(post.getContent())
                .postImageUrls(post.getPostImages().stream()
                        .map(PostImagesResponse::fromImageEntity)
                        .toList())
                .build();
    }

}
