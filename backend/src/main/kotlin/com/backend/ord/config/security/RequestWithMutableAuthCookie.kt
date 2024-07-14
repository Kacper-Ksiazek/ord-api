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
        return originalRequest.cookies.map {
            if (it.name == authCookieName) {
                Cookie(it.name, authCookieValue)
            } else {
                it
            }
        }.toTypedArray()
    }
}
