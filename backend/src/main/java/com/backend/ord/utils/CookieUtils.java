package com.backend.ord.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public final class CookieUtils {
    private final static boolean HTTP_ONLY = true;
    private final static String PATH = "/";


    private CookieUtils() {
    }

    public static void createCookie(
            String name,
            String value,
            HttpServletResponse response
    ) {
        // Instantiate cookie
        Cookie cookie = new Cookie(name, value);

        // Apply common cookie settings
        cookie.setHttpOnly(HTTP_ONLY);
        cookie.setPath(PATH);

        // Add cookie to response
        response.addCookie(cookie);
    }

    public static void deleteCookie(
            String name,
            HttpServletResponse response
    ) {
        // Instantiate cookie
        Cookie cookie = new Cookie(name, "");

        // Apply common cookie settings
        cookie.setHttpOnly(HTTP_ONLY);
        cookie.setPath(PATH);

        // Delete cookie by setting max age to 0
        cookie.setMaxAge(0);

        // Add cookie to response
        response.addCookie(cookie);
    }

    public static Optional<String> getCookieValue(
            String name,
            Cookie[] cookies
    ) {
        // If cookies are null, return empty
        if (cookies == null) {
            return Optional.empty();
        }

        // Find cookie by name
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return Optional.of(cookie.getValue());
            }
        }

        // Otherwise, return empty
        return Optional.empty();
    }

    public static Optional<String> getCookieValue(
            String name,
            HttpServletRequest request
    ) {
        return getCookieValue(name, request.getCookies());
    }
}
