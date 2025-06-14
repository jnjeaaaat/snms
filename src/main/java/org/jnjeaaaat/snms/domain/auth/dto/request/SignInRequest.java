package org.jnjeaaaat.snms.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.jnjeaaaat.snms.global.validator.annotation.Trim;
import org.jnjeaaaat.snms.global.validator.annotation.ValidPassword;
import org.jnjeaaaat.snms.global.validator.annotation.ValidUid;

public record SignInRequest(

        @Trim
        @NotBlank
        @ValidUid
        String uid,

        @NotBlank
        @ValidPassword
        String password

) {

}
