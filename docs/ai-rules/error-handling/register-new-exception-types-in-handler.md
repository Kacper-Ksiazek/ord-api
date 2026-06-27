# Register new exception types in the central handler

If you introduce a new exception in `com.ord.exceptions.REST.*`, you must wire it into `RESTExceptionHandler`: add it to the `@ExceptionHandler(...)` list and add a branch in `getStatusForException` mapping it to its HTTP status. Otherwise it falls through to the catch-all and is reported as a generic 500.

## Good

```kotlin
// RESTExceptionHandler
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
fun handleException(e: Exception): ResponseEntity<HTTPErrorResponse> { /* ... */ }

private fun getStatusForException(e: Exception): Int = when (e) {
    is BadRequestException -> HttpStatus.BAD_REQUEST.value()       // 400
    is NotFoundException   -> HttpStatus.NOT_FOUND.value()         // 404
    is ConflictException   -> HttpStatus.CONFLICT.value()          // 409
    is BadGatewayException -> HttpStatus.BAD_GATEWAY.value()       // 502
    else -> HttpStatus.INTERNAL_SERVER_ERROR.value()              // 500
}
```

## Bad

```kotlin
// New exception type defined but never registered in RESTExceptionHandler:
// it will be swallowed by the catch-all and returned as 500.
class TooManyRequestsException(message: String?) : RuntimeException(message)
```
