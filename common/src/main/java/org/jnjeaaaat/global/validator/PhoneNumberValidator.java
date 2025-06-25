package org.jnjeaaaat.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jnjeaaaat.global.validator.annotation.ValidPhoneNumber;

import static org.jnjeaaaat.global.constant.DtoValid.PHONE_PATTERN;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        // 공백 제거
        String cleanPhoneNumber = phoneNumber.replaceAll("\\s+", "");

        // 하이픈이 포함된 경우 제거하고 검증
        cleanPhoneNumber = cleanPhoneNumber.replaceAll("-", "");

        return PHONE_PATTERN.matcher(cleanPhoneNumber).matches();
    }
}
