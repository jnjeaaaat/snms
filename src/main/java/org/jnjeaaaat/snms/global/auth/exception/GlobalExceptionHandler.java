package org.jnjeaaaat.snms.global.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.snms.global.auth.exception.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(HttpServletRequest request, CustomException e) {
        logError(request, e);

        return ErrorResponse.of(e.getErrorCode(), e.getErrorMessage());
    }

    private void logError(HttpServletRequest request, CustomException e) {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        log.error("[{}] {} : {}", requestMethod, requestUri, e.getErrorMessage());
    }

}
