package com.backend.ord.exceptions.handlers;

import com.backend.ord.exceptions.REST.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

class HTTPErrorResponse {
    private String message;
    private int status;

    HTTPErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public static HTTPErrorResponseBuilder builder() {
        return new HTTPErrorResponseBuilder();
    }

    public String getMessage() {
        return this.message;
    }

    public int getStatus() {
        return this.status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof HTTPErrorResponse)) return false;
        final HTTPErrorResponse other = (HTTPErrorResponse) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        if (this.getStatus() != other.getStatus()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof HTTPErrorResponse;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        result = result * PRIME + this.getStatus();
        return result;
    }

    public String toString() {
        return "HTTPErrorResponse(message=" + this.getMessage() + ", status=" + this.getStatus() + ")";
    }

    public static class HTTPErrorResponseBuilder {
        private String message;
        private int status;

        HTTPErrorResponseBuilder() {
        }

        public HTTPErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public HTTPErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public HTTPErrorResponse build() {
            return new HTTPErrorResponse(this.message, this.status);
        }

        public String toString() {
            return "HTTPErrorResponse.HTTPErrorResponseBuilder(message=" + this.message + ", status=" + this.status + ")";
        }
    }
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
