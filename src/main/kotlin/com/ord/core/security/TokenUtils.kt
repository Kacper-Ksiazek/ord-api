package com.ord.core.security

import org.springframework.http.ResponseCookie
import org.springframework.web.server.ServerWebExchange

fun ServerWebExchange.getCookieValue(
    name: String,
): String? = this.request.cookies.getFirst(name)?.value


fun ServerWebExchange.addAuthTokenCookie(
    name: String,
    value: String,
    secure: Boolean = false,
    sameSite: String = "Lax",
) {
    val cookie = buildAuthTokenCookie(
        name = name,
        value = value,
        secure = secure,
        sameSite = sameSite,
    )

    this.response.addCookie(cookie)
}


fun ServerWebExchange.invalidateAuthTokenCookie(
    name: String,
    secure: Boolean = false,
    sameSite: String = "Lax",
) {
    val cookie = buildAuthTokenCookie(
        name = name,
        value = "",
        secure = secure,
        sameSite = sameSite,
        maxAge = 0,
    )

    this.response.addCookie(cookie)
}

private fun buildAuthTokenCookie(
    name: String,
    value: String,
    secure: Boolean,
    sameSite: String,
    maxAge: Long? = null,
): ResponseCookie {
    val builder = ResponseCookie
        .from(name, value)
        .httpOnly(true)
        .path("/")
        .secure(secure)
        .sameSite(sameSite)

    if (maxAge != null) {
        builder.maxAge(maxAge)
    }

    return builder.build()
}
