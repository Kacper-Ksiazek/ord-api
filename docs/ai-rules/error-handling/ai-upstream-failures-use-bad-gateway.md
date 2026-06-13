# Use BadGatewayException for AI/upstream failures

When an external/upstream dependency fails — most importantly the OpenAI client, or generated content that cannot be produced — represent it with `BadGatewayException` (HTTP 502), not a 400 or a generic 500. This communicates that the request was valid but a downstream service let us down.

## Good

```kotlin
import com.ord.exceptions.REST.BadGatewayException

// OpenAIAPIClientServiceImpl
if (attemptNumber > openAIProperties.maximumNumberOfOpenAIAPIRequestAttempts) {
    return Mono.error(
        BadGatewayException(
            "AI service could not generate a valid response after $attemptNumber attempts. Please try again."
        )
    )
}
```

## Bad

```kotlin
import com.ord.exceptions.REST.BadRequestException

if (attemptNumber > openAIProperties.maximumNumberOfOpenAIAPIRequestAttempts) {
    // Upstream/AI failure is not the client's fault -> 400 is wrong
    return Mono.error(BadRequestException("Could not generate response"))
}
```
