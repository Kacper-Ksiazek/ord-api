package com.backend.ord.exceptions.handlers

import com.backend.ord.exceptions.dto.api_responses.BadRequestResponse
import com.backend.ord.exceptions.dto.api_responses.FieldError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class JakartaAssertionExceptionsHandlers {
    /**
     * Invalid type of parameter
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<BadRequestResponse> {
        return ResponseEntity(
            BadRequestResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "Invalid type of parameter",
                errors = listOf(
                    FieldError(
                        field = "body",
                        errorMessage = e.toString(),
                    )
                )
            ),
            HttpStatus.BAD_REQUEST
        )
    }
}