package com.ord.testing_utils.api.dto

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseCookie
import org.springframework.util.MultiValueMap

data class APIClientResponse<TResponseBody> (
    val body: TResponseBody? = null,
    val status: HttpStatusCode,
    val headers: HttpHeaders,
    val cookies: MultiValueMap<String?, ResponseCookie?>
)