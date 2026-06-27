# Don't leak internals in error messages

Messages passed to domain REST exceptions are returned to the client in `HTTPErrorResponse.message`, so keep them human-friendly and free of stack traces, SQL, class names, or raw upstream payloads. Unexpected/unmapped exceptions are already caught by the catch-all handler in `RESTExceptionHandler`, which logs the details server-side and returns a 500 — let it handle the internals rather than embedding them in your own messages.

## Good

```kotlin
import com.ord.exceptions.REST.NotFoundException

// Friendly, resource-oriented message safe to expose
.switchIfEmpty(Mono.error(NotFoundException("Conversation with id $id not found")))
```

## Bad

```kotlin
import com.ord.exceptions.REST.InternalServerError

.onErrorResume { e ->
    // Leaks stack trace / internal types to the client
    Mono.error(InternalServerError("DB failure: ${e.stackTraceToString()}"))
}
```
