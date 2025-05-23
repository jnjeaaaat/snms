package org.jnjeaaaat.snms.global.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {

    private static String requestUri = "";
    private static String requestMethod = "";

    public static void logInfo(HttpServletRequest request, String description) {
        requestUri = request.getRequestURI();
        requestMethod = request.getMethod();

        log.info("[{}] {} : {}", requestUri, requestMethod, description);
    }

    public static void logError(HttpServletRequest request, Exception e) {
        requestUri = request.getRequestURI();
        requestMethod = request.getMethod();

        log.error("[{}] {} : {}", requestUri, requestMethod, e.getMessage());

    }
}
