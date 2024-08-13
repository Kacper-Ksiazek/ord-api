package com.backend.ord.api.responses

data class FieldError(
    val field: String,
    val errorMessage: String,
)

data class ValidationErrorResponse(
    val status: Int,
    val message: String,
    val errors: List<FieldError>
)