package org.jnjeaaaat.snms.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.snms.global.exception.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.*;
import static org.jnjeaaaat.snms.global.util.LogUtil.logError;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String INVALID_FIELD_REQUEST_FORMAT = "[%s] %s 잘못된 값: %s";
    private static final String MISSING_PATHVARIABLE_FORMAT = "필수 경로 변수가 누락되었습니다: %s";
    private static final String MISSING_PARAM_FORMAT = "필수 파라미터가 누락되었습니다: %s (%s)";
    private static final String INVALID_PARAM_FORMAT = "파라미터 타입이 올바르지 않습니다: %s (요구되는 타입: %s)";

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(HttpServletRequest request, CustomException e) {
        logError(request, e);

        return ErrorResponse.of(e.getErrorCode(), e.getErrorMessage());
    }

    // @Valid로 RequestBody 검증 실패시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {

        log.error("Validation error: ", e);

        FieldError fieldError = e.getFieldErrors().get(0);
        String errorMessage = String.format(
                INVALID_FIELD_REQUEST_FORMAT,
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
        );

        return ErrorResponse.of(INVALID_REQUEST, errorMessage);
    }

    // Bean Validation 실패시
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
        log.error("Validation exception: ", e);

        return ErrorResponse.of(INVALID_REQUEST, e.getMessage());
    }

    // HTTP 메시지 변환 실패시 (JSON 파싱 에러 등)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {

        log.error("HTTP message not readable: ", e);

        return ErrorResponse.of(INVALID_REQUEST, e.getMessage());
    }

    // @Valid로 RequestParam, PathVariable 검증 실패시
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException e) {

        log.error("Constraint violation: ", e);

        return ErrorResponse.of(INVALID_REQUEST, e.getMessage());
    }

    // PathVariable 누락시
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariable(
            MissingPathVariableException e) {

        log.error("Missing path variable: ", e);

        String errorMessage = String.format(
                MISSING_PATHVARIABLE_FORMAT,
                e.getVariableName()
        );

        return ErrorResponse.of(INVALID_REQUEST, errorMessage);
    }

    // RequestParam 누락시
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameter(
            MissingServletRequestParameterException e) {

        log.error("Missing request parameter: ", e);

        String errorMessage = String.format(
                MISSING_PARAM_FORMAT,
                e.getParameterName(),
                e.getParameterType()
        );

        return ErrorResponse.of(INVALID_REQUEST, errorMessage);
    }

    // RequestParam 타입 변환 실패시
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e) {

        log.error("Method argument type mismatch: ", e);

        String errorMessage = String.format(
                INVALID_PARAM_FORMAT,
                e.getName(),
                e.getRequiredType().getSimpleName()
        );

        return ErrorResponse.of(INVALID_REQUEST, errorMessage);
    }

}
