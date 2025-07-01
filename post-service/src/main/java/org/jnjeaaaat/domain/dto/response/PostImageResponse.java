package org.jnjeaaaat.domain.dto.response;

import lombok.Builder;
import org.jnjeaaaat.domain.entity.PostImage;

@Builder
public record PostImageResponse(
        Long id,
        String imageUrl,
        Boolean isThumbnail
) {

    public static PostImageResponse fromEntity(PostImage postImage) {
        return PostImageResponse.builder()
                .id(postImage.getId())
                .imageUrl(postImage.getImageUrl())
                .isThumbnail(postImage.getIsThumbnail())
                .build();
    }

}
