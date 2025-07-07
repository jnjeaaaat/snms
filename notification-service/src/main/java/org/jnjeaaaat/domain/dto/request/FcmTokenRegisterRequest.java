package org.jnjeaaaat.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.jnjeaaaat.global.validator.annotation.Trim;

public record FcmTokenRegisterRequest(

        @Trim
        @NotBlank
        String token
) {

}
