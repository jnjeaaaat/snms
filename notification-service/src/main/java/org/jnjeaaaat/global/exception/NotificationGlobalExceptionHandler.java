package org.jnjeaaaat.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.jnjeaaaat.global.exception.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import static org.jnjeaaaat.global.util.LogUtil.logError;

@ControllerAdvice
public class NotificationGlobalExceptionHandler extends BaseGlobalExceptionHandler {

    @Override
    protected ResponseEntity<ErrorResponse> handleCustomException(HttpServletRequest request, CustomException e) {
        logError(request, e);

        return ErrorResponse.of(e.getErrorCode(), e.getMessage());
    }
}
