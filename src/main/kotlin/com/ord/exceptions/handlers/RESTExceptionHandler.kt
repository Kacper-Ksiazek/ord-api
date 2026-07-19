package com.ord.exceptions.handlers

import com.ord.exceptions.REST.*
import com.ord.exceptions.dto.api_responses.HTTPErrorResponse
import com.ord.shared.utils.Console
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.codec.DecodingException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.ResponseStatusException


@ControllerAdvice
class RESTExceptionHandler(
    private val environment: Environment,
) {
    val logger = LoggerFactory.getLogger(RESTExceptionHandler::class.java)

    private fun printClientError(message: String) {
        if (!environment.matchesProfiles("test")) {
            Console.printRed(message)
        }
    }

    private fun logClientError(message: String, e: Exception) {
        if (environment.matchesProfiles("test")) {
            logger.debug("{}: {}", message, e.message)
        } else {
            logger.error(message, e)
        }
    }

    @ExceptionHandler(
        BadRequestException::class,
        UnauthorizedException::class,
        ForbiddenException::class,
        NotFoundException::class,
        ConflictException::class,
        PayloadTooLargeException::class,
        InternalServerError::class,
        BadGatewayException::class
    )
    fun handleException(e: Exception): ResponseEntity<HTTPErrorResponse> {
        val status = getStatusForException(e)
        val errorResponse = HTTPErrorResponse(message = e.message, status = status)

        return ResponseEntity.status(status).body(errorResponse)
    }

    // Catch-all exception handler
    @ExceptionHandler(Exception::class)
    fun handleUncaughtException(e: Exception): ResponseEntity<HTTPErrorResponse> {
        Console.printRed("\n\uD83D\uDEA8 [500] Internal Server Error: ${e.message}")
        Console.addBreakLine(1)

        logger.error("Uncaught exception: ${e.message}", e)

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

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(e: ServerWebInputException): ResponseEntity<HTTPErrorResponse> {
        val status = HttpStatus.BAD_REQUEST.value()
        val rootCause = e.cause
        val message = when (rootCause) {
            is DecodingException -> {
                // Extract meaningful error from JSON decoding errors
                val originalMessage = rootCause.message ?: "Invalid JSON format"
                when {
                    originalMessage.contains("Cannot deserialize value of type") -> {
                        val enumMatch = Regex("Cannot deserialize value of type `([^`]+)` from String \"([^\"]+)\"").find(originalMessage)
                        if (enumMatch != null) {
                            val (enumType, invalidValue) = enumMatch.destructured
                            val enumName = enumType.substringAfterLast('.')
                            "Invalid value '$invalidValue' for field of type $enumName"
                        } else {
                            "Invalid JSON format in request body"
                        }
                    }
                    else -> "Invalid JSON format in request body"
                }
            }
            else -> e.reason ?: "Invalid request body"
        }

        val errorResponse = HTTPErrorResponse(
            message = message,
            status = status
        )

        printClientError("\n🚨 [$status] Bad Request: $message")
        logClientError("Request parsing error: $message", e)

        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(DecodingException::class)
    fun handleDecodingException(e: DecodingException): ResponseEntity<HTTPErrorResponse> {
        val status = HttpStatus.BAD_REQUEST.value()
        val message = e.message?.let { originalMessage ->
            when {
                originalMessage.contains("Cannot deserialize value of type") -> {
                    val enumMatch = Regex("Cannot deserialize value of type `([^`]+)` from String \"([^\"]+)\"").find(originalMessage)
                    if (enumMatch != null) {
                        val (enumType, invalidValue) = enumMatch.destructured
                        val enumName = enumType.substringAfterLast('.')
                        "Invalid value '$invalidValue' for field of type $enumName"
                    } else {
                        "Invalid JSON format in request body"
                    }
                }
                else -> "Invalid JSON format in request body"
            }
        } ?: "Invalid request format"

        val errorResponse = HTTPErrorResponse(
            message = message,
            status = status
        )

        printClientError("\n🚨 [$status] JSON Decoding Error: $message")
        logClientError("JSON decoding error: $message", e)

        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(MethodNotAllowedException::class)
    fun handleMethodNotAllowedException(e: MethodNotAllowedException): ResponseEntity<HTTPErrorResponse> {
        val status = HttpStatus.METHOD_NOT_ALLOWED.value()
        val supportedMethods = e.supportedMethods.joinToString(", ") { it.name() }
        val message = "HTTP method is not supported for this endpoint. Supported methods: $supportedMethods"

        val errorResponse = HTTPErrorResponse(
            message = message,
            status = status
        )

        printClientError("\n🚨 [$status] Method Not Allowed: $message")
        logClientError("Method not allowed: $message", e)

        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException): ResponseEntity<HTTPErrorResponse> {
        val status = e.statusCode.value()
        val message = e.reason ?: "Resource not found"

        val errorResponse = HTTPErrorResponse(
            message = message,
            status = status
        )

        printClientError("\n🚨 [$status] ${e.statusCode}: $message")
        logClientError("Response status exception: $message", e)

        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(e: DuplicateKeyException): ResponseEntity<HTTPErrorResponse> {
        val status = HttpStatus.BAD_REQUEST.value()
        val message = e.message?.let { msg ->
            when {
                msg.contains("each_user_can_have_only_one_proficiency_per_language") ->
                    "Language proficiency already exists for this user"
                msg.contains("duplicate key") -> "Resource already exists"
                else -> "Duplicate resource violation"
            }
        } ?: "Duplicate resource violation"

        val errorResponse = HTTPErrorResponse(
            message = message,
            status = status
        )

        printClientError("\n🚨 [$status] Duplicate Key: $message")
        logClientError("Duplicate key violation: $message", e)

        return ResponseEntity.status(status).body(errorResponse)
    }


    private fun getStatusForException(e: Exception): Int = when (e) {
        is BadRequestException -> HttpStatus.BAD_REQUEST.value() // 400
        is UnauthorizedException -> HttpStatus.UNAUTHORIZED.value() // 401
        is ForbiddenException -> HttpStatus.FORBIDDEN.value() // 403
        is NotFoundException -> HttpStatus.NOT_FOUND.value() // 404
        is ConflictException -> HttpStatus.CONFLICT.value() // 409
        is PayloadTooLargeException -> HttpStatus.PAYLOAD_TOO_LARGE.value() // 413
        is InternalServerError -> HttpStatus.INTERNAL_SERVER_ERROR.value() // 500
        is BadGatewayException -> HttpStatus.BAD_GATEWAY.value() // 502
        else -> HttpStatus.INTERNAL_SERVER_ERROR.value() // 500
    }
}
