package org.jnjeaaaat.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.jnjeaaaat.global.validator.annotation.ValidPhoneNumber;

import static org.jnjeaaaat.global.constant.DtoValid.AUTH_CODE_MESSAGE;
import static org.jnjeaaaat.global.constant.DtoValid.AUTH_CODE_REGEX;

public record SmsVerifyRequest(

        @NotBlank
        @ValidPhoneNumber
        String phoneNum,

        @NotBlank
        @Pattern(regexp = AUTH_CODE_REGEX, message = AUTH_CODE_MESSAGE)
        String authCode
) {

}
