# Throw domain REST exceptions, not raw framework exceptions

Signal HTTP error conditions by throwing the domain exceptions from `com.ord.exceptions.REST.*` (`BadRequestException`, `UnauthorizedException`, `ForbiddenException`, `NotFoundException`, `ConflictException`, `PayloadTooLargeException`, `InternalServerError`, `BadGatewayException`). These are mapped to the correct HTTP status centrally in `RESTExceptionHandler`, so never throw a raw `ResponseStatusException` or a generic `RuntimeException`/`IllegalStateException` for expected error conditions.

## Good

```kotlin
import com.ord.exceptions.REST.ConflictException
import com.ord.exceptions.REST.NotFoundException

// WordDetailsFacadeImpl
.flatMap { exists ->
    if (exists) {
        Mono.error(ConflictException("Word details already exist for word with id $wordId"))
    } else {
        val wordDetailsToSave = WordDetailsEntity(/* ... */)
        repository.save(wordDetailsToSave)
    }
}
```

## Bad

```kotlin
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus

.flatMap { exists ->
    if (exists) {
        // Raw framework / generic exceptions bypass the domain hierarchy
        Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "Already exists"))
    } else {
        repository.save(wordDetailsToSave)
    }
}
```
