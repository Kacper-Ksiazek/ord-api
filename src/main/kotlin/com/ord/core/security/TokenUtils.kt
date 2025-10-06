package com.ord.core.security

import org.springframework.http.ResponseCookie
import org.springframework.web.server.ServerWebExchange

fun ServerWebExchange.getCookieValue(
    name: String,
): String? = this.request.cookies.getFirst(name)?.value


fun ServerWebExchange.addAuthTokenCookie(
    name: String,
    value: String
) {
    val cookie = ResponseCookie
        .from(name, value)
        .httpOnly(true)
        .path("/")
        .build()

    this.response.addCookie(cookie)
}


fun ServerWebExchange.invalidateAuthTokenCookie(
    name: String
) {
    val cookie = ResponseCookie
        .from(name, "")
        .httpOnly(true)
        .path("/")
        .maxAge(0)
        .build()

    this.response.addCookie(cookie)
}