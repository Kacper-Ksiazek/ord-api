package com.backend.ord.exceptions.handlers

import com.backend.ord.api.responses.FieldError
import com.backend.ord.api.responses.ValidationErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class MissingServletRequestParameterExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingParametersRequestException(
        exception: MissingServletRequestParameterException
    ): ResponseEntity<ValidationErrorResponse> {

        return ResponseEntity(
            ValidationErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "Missing request parameter",
                errors = listOf(
                    FieldError(
                        field = exception.parameterName,
                        errorMessage = exception.message ?: "Missing request parameter"
                    )
                )
            ),
            HttpStatus.BAD_REQUEST
        )

    }
}