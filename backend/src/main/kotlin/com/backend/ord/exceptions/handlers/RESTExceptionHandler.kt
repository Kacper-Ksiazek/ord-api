package com.backend.ord.exceptions.handlers

import com.backend.ord.api.responses.HTTPErrorResponse
import com.backend.ord.exceptions.REST.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class RESTExceptionHandler {

    // Handling multiple exceptions with a single method
    @ExceptionHandler(
        BadRequestException::class,
        UnauthorizedException::class,
        ForbiddenException::class,
        NotFoundException::class,
        PayloadTooLargeException::class,
        InternalServerError::class
    )
    fun handleException(
        e: Exception,
        status: Int = getStatusForException(e)
    ): ResponseEntity<HTTPErrorResponse> {
        val errorResponse = HTTPErrorResponse(message = e.message, status = status)
        return ResponseEntity.status(status).body(errorResponse)
    }

    // Mapping exceptions to their respective HTTP status codes
    private fun getStatusForException(e: Exception): Int = when (e) {
        is BadRequestException -> 400
        is UnauthorizedException -> 401
        is ForbiddenException -> 403
        is NotFoundException -> 404
        is PayloadTooLargeException -> 413
        is InternalServerError -> 500
        else -> 500 // Fallback to Internal Server Error for any unknown exceptions
    }
}

