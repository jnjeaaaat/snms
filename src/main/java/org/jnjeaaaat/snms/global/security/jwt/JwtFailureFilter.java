package org.jnjeaaaat.snms.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.snms.global.exception.ErrorCode;
import org.jnjeaaaat.snms.global.exception.dto.ErrorResponse;
import org.jnjeaaaat.snms.global.security.jwt.exception.TokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFailureFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e) {
            sendErrorResponse(e.getErrorCode(), response);
        }
    }

    private void sendErrorResponse(ErrorCode errorCode, HttpServletResponse response)
            throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorCode.getErrorMessage());
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
