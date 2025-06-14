package org.jnjeaaaat.snms.global.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.jnjeaaaat.snms.global.validator.FileValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class)
public @interface ValidFile {

    String message() default "{validation.constraints.ValidFile.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long maxSize() default 5 * 1024 * 1024; // 5MB

    String[] allowedTypes() default {
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    };

    boolean allowEmpty() default false;
}
