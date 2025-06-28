package org.jnjeaaaat.domain.dto.response;

import lombok.Builder;
import org.jnjeaaaat.domain.entity.PostImages;

@Builder
public record PostImagesResponse(
        Long id,
        String imageUrl,
        Boolean isThumbnail
) {

    public static PostImagesResponse fromEntity(PostImages postImages) {
        return PostImagesResponse.builder()
                .id(postImages.getId())
                .imageUrl(postImages.getImageUrl())
                .isThumbnail(postImages.getIsThumbnail())
                .build();
    }

}
