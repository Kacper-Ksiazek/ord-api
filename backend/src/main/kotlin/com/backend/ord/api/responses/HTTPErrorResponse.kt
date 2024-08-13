package com.backend.ord.api.responses

data class HTTPErrorResponse(
    val message: String?,
    val status: Int
)