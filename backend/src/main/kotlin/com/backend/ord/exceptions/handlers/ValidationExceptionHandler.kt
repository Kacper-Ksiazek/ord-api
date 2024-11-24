package com.backend.ord.exceptions.handlers

import com.backend.ord.api.responses.FieldError
import com.backend.ord.api.responses.BadRequestResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * TODO: Add docs
 */
@ControllerAdvice
class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(
        exception: MethodArgumentNotValidException
    ): ResponseEntity<BadRequestResponse> {
        val errors = exception.bindingResult.fieldErrors.map {
            FieldError(
                field = it.field,
                errorMessage = it.defaultMessage ?: "Invalid value"
            )
        }

        return ResponseEntity(
            BadRequestResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "Validation failed",
                errors = errors
            ),
            HttpStatus.BAD_REQUEST
        )
    }
}