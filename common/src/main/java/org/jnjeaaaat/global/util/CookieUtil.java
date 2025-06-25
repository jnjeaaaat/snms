package org.jnjeaaaat.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // todo: true 로 변경
                .path("/")
                .sameSite("None")
                .maxAge(maxAge)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findFirst();
        }

        return Optional.empty();
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name)
                .httpOnly(true)
                .secure(false) // todo: true 로 변경
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
