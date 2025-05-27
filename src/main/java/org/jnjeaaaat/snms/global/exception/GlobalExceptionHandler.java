package org.jnjeaaaat.snms.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.snms.global.exception.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.jnjeaaaat.snms.global.util.LogUtil.logError;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(HttpServletRequest request, CustomException e) {
        logError(request, e);

        return ErrorResponse.of(e.getErrorCode(), e.getErrorMessage());
    }

}
