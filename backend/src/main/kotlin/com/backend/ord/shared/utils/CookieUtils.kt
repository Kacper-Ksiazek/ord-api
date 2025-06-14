package com.backend.ord.shared.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

object CookieUtils {
    private const val HTTP_ONLY = true
    private const val PATH = "/"

    fun createCookie(
        name: String,
        value: String,
        response: HttpServletResponse
    ) {
        // Instantiate cookie
        val cookie = Cookie(name, value)

        // Apply common cookie settings
        cookie.isHttpOnly = HTTP_ONLY
        cookie.path = PATH

        // Add cookie to response
        response.addCookie(cookie)
    }

    fun deleteCookie(
        name: String,
        response: HttpServletResponse
    ) {
        // Instantiate cookie
        val cookie = Cookie(name, "")

        // Apply common cookie settings
        cookie.isHttpOnly = HTTP_ONLY
        cookie.path = PATH

        // Delete cookie by setting max age to 0
        cookie.maxAge = 0

        // Add cookie to response
        response.addCookie(cookie)
    }

    fun getCookieValue(
        name: String,
        cookies: Array<Cookie>?
    ): String? {
        // If cookies are null, return empty
        if (cookies == null) {
            return null
        }

        // Find cookie by name
        for (cookie in cookies) {
            if (cookie.name == name) {
                return cookie.value
            }
        }

        return null
    }

    fun getCookieValue(
        name: String,
        request: HttpServletRequest
    ): String? {
        return getCookieValue(name, request.cookies)
    }
}
