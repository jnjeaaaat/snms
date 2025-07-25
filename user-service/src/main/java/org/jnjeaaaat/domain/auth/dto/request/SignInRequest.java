package org.jnjeaaaat.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.jnjeaaaat.global.validator.annotation.Trim;
import org.jnjeaaaat.global.validator.annotation.ValidPassword;
import org.jnjeaaaat.global.validator.annotation.ValidUid;

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
