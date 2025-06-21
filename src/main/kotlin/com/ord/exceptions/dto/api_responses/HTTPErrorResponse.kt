package com.ord.exceptions.dto.api_responses

data class HTTPErrorResponse(
    val message: String?,
    val status: Int
)