package org.jnjeaaaat.domain.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank
        @Size(max = 500, message = "{validation.constraints.MaxLength500.message}")
        String content
) {

}
