package org.jnjeaaaat.snms.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.jnjeaaaat.snms.global.validator.annotation.Trim;
import org.jnjeaaaat.snms.global.validator.annotation.ValidUid;

public record UidCheckRequest(

        @Trim
        @NotBlank
        @ValidUid
        String uid
) {

}
