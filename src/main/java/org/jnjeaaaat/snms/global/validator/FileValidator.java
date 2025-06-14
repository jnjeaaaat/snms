package org.jnjeaaaat.snms.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jnjeaaaat.snms.global.validator.annotation.ValidFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private long maxSize;
    private List<String> allowedTypes;
    private boolean allowEmpty;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowedTypes());
        this.allowEmpty = constraintAnnotation.allowEmpty();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // null 체크
        if (file == null) {
            addViolation(context, "파일을 업로드 해주세요.");
            return false;
        }

        // 빈 파일 체크
        if (file.isEmpty()) {
            if (allowEmpty) {
                return true;
            }
            addViolation(context, "파일이 비어있습니다");
            return false;
        }

        // 파일 크기 체크
        if (file.getSize() > maxSize) {
            addViolation(context,
                    String.format("파일 크기가 %dMB를 초과합니다", maxSize / (1024 * 1024)));
            return false;
        }

        // Content-Type 체크
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            addViolation(context,
                    String.format("지원하지 않는 파일 형식입니다. 허용 형식: %s",
                            String.join(", ", allowedTypes)));
            return false;
        }

        // 실제 파일 내용 검증 (Magic Number 체크)
        if (!isValidImageContent(file)) {
            addViolation(context, "파일 내용이 이미지 형식과 일치하지 않습니다");
            return false;
        }

        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    /**
     * 파일의 Magic Number를 확인하여 실제 이미지 파일인지 검증
     */
    private boolean isValidImageContent(MultipartFile file) {
        try {
            byte[] bytes = new byte[8];
            int bytesRead = file.getInputStream().read(bytes);

            if (bytesRead < 2) {
                return false;
            }

            // JPEG: FF D8
            if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8) {
                return true;
            }

            // PNG: 89 50 4E 47
            if (bytesRead >= 4 &&
                    bytes[0] == (byte) 0x89 && bytes[1] == 0x50 &&
                    bytes[2] == 0x4E && bytes[3] == 0x47) {
                return true;
            }

            // GIF: 47 49 46 38
            if (bytesRead >= 4 &&
                    bytes[0] == 0x47 && bytes[1] == 0x49 &&
                    bytes[2] == 0x46 && bytes[3] == 0x38) {
                return true;
            }

            // WebP: 52 49 46 46 ... 57 45 42 50
            if (bytesRead >= 8 &&
                    bytes[0] == 0x52 && bytes[1] == 0x49 &&
                    bytes[2] == 0x46 && bytes[3] == 0x46 &&
                    bytes[8] == 0x57 && bytes[9] == 0x45 &&
                    bytes[10] == 0x42 && bytes[11] == 0x50) {
                return true;
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }
}