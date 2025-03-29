package com.backend.ord.exceptions.handlers

import com.backend.ord.api.responses.HTTPErrorResponse
import com.backend.ord.exceptions.REST.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
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

    // Catch-all exception handler
    @ExceptionHandler(Exception::class)
    fun handleUncaughtException(e: Exception): ResponseEntity<HTTPErrorResponse> {
        // TODO: Improve error logging
        val errorResponse = HTTPErrorResponse(
            message = """
                Unexpected error occurred
                - Exception: $e
                - Message: ${e.message}
                - Cause: ${e.cause}
                - Stack trace: ${e.stackTrace}
                - Class: ${e.javaClass}
                - Localized message: ${e.localizedMessage}
                - Suppressed: ${e.suppressed}
            """,

            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<HTTPErrorResponse> {
        val status = HttpStatus.BAD_REQUEST.value()
        val errorResponse = HTTPErrorResponse(
            message = e.bindingResult.allErrors.joinToString { it.defaultMessage ?: "" },
            status = status
        )

        return ResponseEntity.status(status).body(errorResponse)
    }

    private fun getStatusForException(e: Exception): Int = when (e) {
        is BadRequestException -> HttpStatus.BAD_REQUEST.value() // 400
        is UnauthorizedException -> HttpStatus.UNAUTHORIZED.value() // 401
        is ForbiddenException -> HttpStatus.FORBIDDEN.value() // 403
        is NotFoundException -> HttpStatus.NOT_FOUND.value() // 404
        is PayloadTooLargeException -> HttpStatus.PAYLOAD_TOO_LARGE.value() // 413
        is InternalServerError -> HttpStatus.INTERNAL_SERVER_ERROR.value() // 500
        is BadGatewayException -> HttpStatus.BAD_GATEWAY.value() // 502
        else -> HttpStatus.INTERNAL_SERVER_ERROR.value() // 500
    }
}
