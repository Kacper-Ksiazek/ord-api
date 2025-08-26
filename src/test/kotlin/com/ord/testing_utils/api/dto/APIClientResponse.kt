package com.ord.testing_utils.api.dto

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

data class APIClientResponse<TResponseBody> (
    val body: TResponseBody? = null,
    val status: HttpStatusCode,
    val headers: HttpHeaders
)