package com.backend.ord.exceptions.handlers

import com.backend.ord.api.responses.HTTPErrorResponse
import com.backend.ord.exceptions.REST.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class RESTExceptionHandler {

    @ExceptionHandler(
        BadRequestException::class,
        UnauthorizedException::class,
        ForbiddenException::class,
        NotFoundException::class,
        PayloadTooLargeException::class,
        InternalServerError::class
    )
    fun handleException(e: Exception): ResponseEntity<HTTPErrorResponse> {
        val status = getStatusForException(e)
        val errorResponse = HTTPErrorResponse(message = e.message, status = status)
        return ResponseEntity.status(status).body(errorResponse)
    }

    private fun getStatusForException(e: Exception): Int = when (e) {
        is BadRequestException -> 400
        is UnauthorizedException -> 401
        is ForbiddenException -> 403
        is NotFoundException -> 404
        is PayloadTooLargeException -> 413
        is InternalServerError -> 500
        else -> 500
    }
}
