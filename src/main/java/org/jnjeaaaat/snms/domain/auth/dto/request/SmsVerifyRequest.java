package org.jnjeaaaat.snms.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.jnjeaaaat.snms.global.validator.annotation.ValidPhoneNumber;

import static org.jnjeaaaat.snms.global.constant.DtoValid.AUTH_CODE_MESSAGE;
import static org.jnjeaaaat.snms.global.constant.DtoValid.AUTH_CODE_REGEX;

public record SmsVerifyRequest(

        @NotBlank
        @ValidPhoneNumber
        String phoneNum,

        @NotBlank
        @Pattern(regexp = AUTH_CODE_REGEX, message = AUTH_CODE_MESSAGE)
        String authCode
) {

}
