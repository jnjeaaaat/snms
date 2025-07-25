package org.jnjeaaaat.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.jnjeaaaat.global.validator.annotation.ValidPhoneNumber;

public record SmsSendRequest(

        @NotBlank
        @ValidPhoneNumber
        String phoneNum
) {

}
