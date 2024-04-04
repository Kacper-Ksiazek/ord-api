package com.backend.ord.exceptions.handlers;

import com.backend.ord.exceptions.REST.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Data
@Builder
class HTTPErrorResponse {
    private String message;
    private int status;
}

@ControllerAdvice
public class RESTExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        return handleException(e, 400);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException e) {
        return handleException(e, 401);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        return handleException(e, 403);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return handleException(e, 404);
    }

    @ExceptionHandler(PayloadTooLargeException.class)
    public ResponseEntity<?> handlePayloadTooLargeException(PayloadTooLargeException e) {
        return handleException(e, 413);
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<?> handleInternalServerError(InternalServerError e) {
        return handleException(e, 500);
    }

    private ResponseEntity<?> handleException(Exception e, int status) {
        return ResponseEntity
                .status(status)
                .body(
                        HTTPErrorResponse.builder()
                                .message(e.getMessage())
                                .status(status)
                                .build()
                );
    }
}
