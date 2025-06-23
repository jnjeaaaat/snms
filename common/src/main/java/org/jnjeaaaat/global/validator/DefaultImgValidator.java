package org.jnjeaaaat.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jnjeaaaat.global.validator.annotation.ValidDefaultImg;

import static org.jnjeaaaat.global.constant.DtoValid.DEFAULT_IMG_SUFFIX;

public class DefaultImgValidator implements ConstraintValidator<ValidDefaultImg, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.trim().isEmpty()) {
            return false;
        }

        // /default.jpg로 끝나는지 확인
        return value.endsWith(DEFAULT_IMG_SUFFIX);
    }

}
