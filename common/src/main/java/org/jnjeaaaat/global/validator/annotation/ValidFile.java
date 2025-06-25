package org.jnjeaaaat.global.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.jnjeaaaat.global.validator.FileValidator;

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

    /**
     * 허용된 이미지 타입들
     */
    String[] allowedTypes() default {
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    long maxSizePerFile() default 5;

    long maxTotalSize() default 40;

    /**
     * null 값 허용 여부
     */
    boolean allowNull() default true;
}
