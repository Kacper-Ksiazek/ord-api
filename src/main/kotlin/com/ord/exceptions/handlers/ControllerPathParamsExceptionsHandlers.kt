package com.ord.exceptions.handlers

import com.ord.exceptions.dto.api_responses.BadRequestResponse
import com.ord.exceptions.dto.api_responses.FieldError
import com.ord.exceptions.dto.api_responses.InvalidTypeFieldError
import com.ord.exceptions.dto.api_responses.ValidationFieldError
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException


@ControllerAdvice
class HandlerMethodValidationExceptionHandler {
    /**
     * Parameter's value did not match set requirements, eg: is too small
     */
    @ExceptionHandler(HandlerMethodValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handeValidationException(
        exception: HandlerMethodValidationException
    ): ResponseEntity<BadRequestResponse> {
        exception.allValidationResults.map { issue ->
            val receivedValue = issue.argument
            val failedParameterName =
                (issue.resolvableErrors.first().arguments?.first() as DefaultMessageSourceResolvable).defaultMessage
            val problem = issue.resolvableErrors.first().defaultMessage

            "Validation failed! Property $failedParameterName received a value $receivedValue, but: $problem"
        }

        return ResponseEntity(
            BadRequestResponse(
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

    /**
     * Required parameter is missing
     */
//    @ExceptionHandler(MissingServletRequestParameterException::class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    fun handleMissingParametersException(
//        exception: MissingServletRequestParameterException
//    ): ResponseEntity<BadRequestResponse> {
//        return ResponseEntity(
//            BadRequestResponse(
//                status = HttpStatus.BAD_REQUEST.value(),
//                message = "Missing request parameter",
//                errors = listOf(
//                    FieldError(
//                        field = exception.parameterName,
//                        errorMessage = exception.message ?: "Missing request parameter"
//                    )
//                )
//            ),
//            HttpStatus.BAD_REQUEST
//        )
//    }

    /**
     * Parameter's type is invalid
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidParameterTypeException(
        exception: MethodArgumentTypeMismatchException
    ): ResponseEntity<BadRequestResponse> {
        // TODO: Verify that it also works properly with enum types

        return ResponseEntity(
            BadRequestResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "Invalid parameter's type",
                listOf(
                    InvalidTypeFieldError(
                        field = exception.name,
                        expectedType = exception.requiredType.name,
                        receivedValue = exception.value!!.toString(),
                        errorMessage = exception.message!!
                    )
                ),
            ),
            HttpStatus.BAD_REQUEST
        )
    }
}
