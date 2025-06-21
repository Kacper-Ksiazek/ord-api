package com.ord.exceptions.dto.api_responses

open class FieldError(
    open val field: String,
    open val errorMessage: String,
)

data class ValidationFieldError(
    override val field: String,
    override val errorMessage: String,
    val receivedValue: String
) : FieldError(field, errorMessage)

data class InvalidTypeFieldError(
    override val field: String,
    override val errorMessage: String,
    val receivedValue: String,
    val expectedType: String
) : FieldError(field, errorMessage)

data class BadRequestResponse(
    val status: Int,
    val message: String,
    val errors: List<FieldError>
)