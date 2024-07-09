package com.backend.ord.exceptions.handlers

import com.backend.ord.exceptions.REST.*
import com.backend.ord.exceptions.handlers.HTTPErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

internal class HTTPErrorResponse(var message: String?, var status: Int) {
    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is HTTPErrorResponse) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        val `this$message`: Any? = this.message
        val `other$message`: Any? = other.message
        if (if (`this$message` == null) `other$message` != null else `this$message` != `other$message`) return false
        if (this.status != other.status) return false
        return true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is HTTPErrorResponse
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$message`: Any? = this.message
        result = result * PRIME + (`$message`?.hashCode() ?: 43)
        result = result * PRIME + this.status
        return result
    }

    override fun toString(): String {
        return "HTTPErrorResponse(message=" + this.message + ", status=" + this.status + ")"
    }

    class HTTPErrorResponseBuilder internal constructor() {
        private var message: String? = null
        private var status = 0

        fun message(message: String?): HTTPErrorResponseBuilder {
            this.message = message
            return this
        }

        fun status(status: Int): HTTPErrorResponseBuilder {
            this.status = status
            return this
        }

        fun build(): HTTPErrorResponse {
            return HTTPErrorResponse(this.message, this.status)
        }

        override fun toString(): String {
            return "HTTPErrorResponse.HTTPErrorResponseBuilder(message=" + this.message + ", status=" + this.status + ")"
        }
    }

    companion object {
        fun builder(): HTTPErrorResponseBuilder {
            return HTTPErrorResponseBuilder()
        }
    }
}

@ControllerAdvice
class RESTExceptionHandler {
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<*> {
        return handleException(e, 400)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<*> {
        return handleException(e, 401)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(e: ForbiddenException): ResponseEntity<*> {
        return handleException(e, 403)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<*> {
        return handleException(e, 404)
    }

    @ExceptionHandler(PayloadTooLargeException::class)
    fun handlePayloadTooLargeException(e: PayloadTooLargeException): ResponseEntity<*> {
        return handleException(e, 413)
    }

    @ExceptionHandler(InternalServerError::class)
    fun handleInternalServerError(e: InternalServerError): ResponseEntity<*> {
        return handleException(e, 500)
    }

    private fun handleException(e: Exception, status: Int): ResponseEntity<*> {
        return ResponseEntity
            .status(status)
            .body(
                HTTPErrorResponse.builder()
                    .message(e.message)
                    .status(status)
                    .build()
            )
    }
}
