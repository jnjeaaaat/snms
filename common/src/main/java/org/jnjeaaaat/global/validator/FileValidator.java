package org.jnjeaaaat.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jnjeaaaat.global.validator.annotation.ValidFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FileValidator implements ConstraintValidator<ValidFile, Object> {

    private Set<String> allowedTypes;
    private long maxSizePerFile;
    private long maxTotalSize;
    private boolean allowNull;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.allowedTypes = Set.of(constraintAnnotation.allowedTypes());
        this.maxSizePerFile = constraintAnnotation.maxSizePerFile() * 1024 * 1024; // MB to bytes
        this.maxTotalSize = constraintAnnotation.maxTotalSize() * 1024 * 1024; // MB to bytes
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // null 체크
        if (value == null) {
            return allowNull;
        }

        // 단일 파일 검증
        if (value instanceof MultipartFile) {
            return validateSingleFile((MultipartFile) value, context);
        }

        // 파일 배열 검증
        if (value instanceof MultipartFile[]) {
            return validateMultipleFiles(Arrays.asList((MultipartFile[]) value), context);
        }

        // 파일 리스트 검증
        if (value instanceof List<?> list) {
            if (list.isEmpty()) {
                return allowNull;
            }

            // 리스트의 첫 번째 요소가 MultipartFile인지 확인
            if (list.get(0) instanceof MultipartFile) {
                @SuppressWarnings("unchecked")
                List<MultipartFile> files = (List<MultipartFile>) list;
                return validateMultipleFiles(files, context);
            }
        }

        // 지원하지 않는 타입
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("지원하지 않는 파일 타입입니다")
                .addConstraintViolation();
        return false;
    }

    private boolean validateSingleFile(MultipartFile file, ConstraintValidatorContext context) {
        // 빈 파일 체크
        if (file.isEmpty()) {
            return allowNull;
        }

        // Content-Type 검증
        if (isValidContentType(file.getContentType())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            String.format("허용되지 않는 파일 형식입니다. 허용 형식: %s",
                                    String.join(", ", allowedTypes)))
                    .addConstraintViolation();
            return false;
        }

        // 파일 크기 검증
        if (file.getSize() > maxSizePerFile) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            String.format("파일 크기가 너무 큽니다. 최대 %dMB까지 허용됩니다",
                                    maxSizePerFile / (1024 * 1024)))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean validateMultipleFiles(List<MultipartFile> files, ConstraintValidatorContext context) {
        // 빈 리스트 체크
        if (files.isEmpty()) {
            return allowNull;
        }

        long totalSize = 0;

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            // 빈 파일은 스킵
            if (file.isEmpty()) {
                continue;
            }

            // Content-Type 검증
            if (isValidContentType(file.getContentType())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                String.format("[파일 %d] 허용되지 않는 파일 형식입니다. 허용 형식: %s",
                                        i + 1, String.join(", ", allowedTypes)))
                        .addConstraintViolation();
                return false;
            }

            // 개별 파일 크기 검증
            if (file.getSize() > maxSizePerFile) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                String.format("[파일 %d] 파일 크기가 너무 큽니다. 최대 %dMB까지 허용됩니다",
                                        i + 1, maxSizePerFile / (1024 * 1024)))
                        .addConstraintViolation();
                return false;
            }

            totalSize += file.getSize();
        }

        // 전체 파일 크기 검증
        if (totalSize > maxTotalSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            String.format("전체 파일 크기가 너무 큽니다. 최대 %dMB까지 허용됩니다",
                                    maxTotalSize / (1024 * 1024)))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean isValidContentType(String contentType) {
        return contentType == null || !allowedTypes.contains(contentType.toLowerCase());
    }
}