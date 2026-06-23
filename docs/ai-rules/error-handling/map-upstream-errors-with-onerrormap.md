# Map low-level exceptions into domain REST exceptions with onErrorMap

When a lower layer throws a non-domain exception (e.g. `IllegalArgumentException` from board/puzzle validation), translate it into the appropriate domain REST exception at the boundary using `onErrorMap`, instead of letting it fall through to the catch-all 500 handler. This keeps the externally returned status meaningful.

## Good

```kotlin
import com.ord.exceptions.REST.BadGatewayException

// CrosswordGameFacade
.onErrorMap { e ->
    when (e) {
        is IllegalArgumentException -> BadGatewayException(
            "Failed to generate crossword puzzle: ${e.message}"
        )
        else -> e
    }
}
```

## Bad

```kotlin
// No mapping: an IllegalArgumentException from generation leaks to the
// catch-all handler and is reported as a generic 500.
crosswordGenerator.generate(/* ... */)
```
