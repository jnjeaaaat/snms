package org.jnjeaaaat.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.jnjeaaaat.global.validator.annotation.Trim;
import org.jnjeaaaat.global.validator.annotation.ValidUid;

public record UidCheckRequest(

        @Trim
        @NotBlank
        @ValidUid
        String uid
) {

}
