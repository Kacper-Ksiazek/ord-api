package com.backend.ord.api.responses

open class FieldError(
    open val field: String,
    open val errorMessage: String,
)

data class ValidationFieldError(
    override val field: String,
    override val errorMessage: String,
    val receivedValue: String
) : FieldError(field, errorMessage)

data class ValidationErrorResponse(
    val status: Int,
    val message: String,
    val errors: List<FieldError>
)