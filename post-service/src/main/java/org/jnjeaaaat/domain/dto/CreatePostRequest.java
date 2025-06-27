package org.jnjeaaaat.domain.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @Nullable
        @Size(max = 500, message = "{validation.constraints.MaxLength500.message}")
        String content
) {

}
