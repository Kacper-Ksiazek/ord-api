package com.backend.ord.config.security

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper

class RequestWithMutableAuthCookie(
    private val originalRequest: HttpServletRequest,
    private val authCookieName: String?
) : HttpServletRequestWrapper(
    originalRequest
) {
    var authCookieValue: String? = null

    override fun getCookies(): Array<Cookie> {
        val cookies = originalRequest.cookies

        if (authCookieValue != null) {
            for (cookie in cookies) {
                if (cookie.name == authCookieName) {
                    cookie.value = authCookieValue
                }
            }
        }
        return cookies
    }
}
