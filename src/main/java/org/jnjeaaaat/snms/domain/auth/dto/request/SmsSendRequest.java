package org.jnjeaaaat.snms.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.jnjeaaaat.snms.global.validator.annotation.ValidPhoneNumber;

public record SmsSendRequest(

        @NotBlank
        @ValidPhoneNumber
        String phoneNum
) {

}
