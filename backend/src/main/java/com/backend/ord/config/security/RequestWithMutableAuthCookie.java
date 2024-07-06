package com.backend.ord.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class RequestWithMutableAuthCookie extends HttpServletRequestWrapper {
    private final HttpServletRequest originalRequest;
    private final String authCookieName;

    private String authCookieValue;

    public RequestWithMutableAuthCookie(HttpServletRequest request, String authCookieName) {
        super(request);
        this.originalRequest = request;
        this.authCookieName = authCookieName;
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] cookies = originalRequest.getCookies();

        if (authCookieValue != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(authCookieName)) {
                    cookie.setValue(authCookieValue);
                }
            }
        }
        return cookies;
    }

    public String getAuthCookieValue() {
        return this.authCookieValue;
    }

    public void setAuthCookieValue(String authCookieValue) {
        this.authCookieValue = authCookieValue;
    }
}
