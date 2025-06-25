package org.jnjeaaaat.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jnjeaaaat.global.validator.annotation.ValidProviderName;

import static org.jnjeaaaat.global.constant.DtoValid.VALID_PROVIDERS;

public class ProviderNameValidator implements ConstraintValidator<ValidProviderName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return value == null || VALID_PROVIDERS.contains(value.toLowerCase());
    }

}
