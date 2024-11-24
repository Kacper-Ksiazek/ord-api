package com.backend.ord.exceptions.handlers

import com.backend.ord.api.responses.ValidationErrorResponse
import com.backend.ord.api.responses.ValidationFieldError
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.HandlerMethodValidationException


@ControllerAdvice
class HandlerMethodValidationExceptionHandler {
    @ExceptionHandler(HandlerMethodValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handeException(
        exception: HandlerMethodValidationException
    ): ResponseEntity<ValidationErrorResponse> {
        exception.allValidationResults.map { issue ->
            val receivedValue = issue.argument
            val failedParameterName =
                (issue.resolvableErrors.first().arguments?.first() as DefaultMessageSourceResolvable).defaultMessage
            val problem = issue.resolvableErrors.first().defaultMessage

            "Validation failed! Property $failedParameterName received a value $receivedValue, but: $problem"
        }

        return ResponseEntity(
            ValidationErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "Validation failed",
                errors = exception.allValidationResults.map { issue ->
                    val receivedValue = issue.argument
                    val failedParameterName =
                        (issue.resolvableErrors.first().arguments?.first() as DefaultMessageSourceResolvable).defaultMessage!!
                    val problem = issue.resolvableErrors.first().defaultMessage

                    ValidationFieldError(
                        field = failedParameterName,
                        receivedValue = receivedValue!!.toString(),
                        errorMessage = problem!!.replaceFirstChar { it.uppercase() }
                    )
                }


            ),
            HttpStatus.BAD_REQUEST
        )
    }
}
