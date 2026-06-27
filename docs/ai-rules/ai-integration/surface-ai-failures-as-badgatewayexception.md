# Let AI failures surface as BadGatewayException (502)

When the AI provider cannot produce a usable response, the failure is an upstream-dependency problem, not a client error. The client already maps exhausted retries to `BadGatewayException`, which `RESTExceptionHandler` translates to HTTP 502. Don't swallow AI errors into a 200 with empty data, and don't remap them to 400/500. If you must short-circuit a missing/null AI payload yourself, throw `BadGatewayException` (or `OpenAIResponseIsNullException`) rather than a generic exception so the 502 contract is preserved.

## Good

```kotlin
// In OpenAIAPIClientServiceImpl: retries exhausted -> 502 to the caller.
if (attemptNumber > openAIProperties.maximumNumberOfOpenAIAPIRequestAttempts) {
    return Mono.error(
        BadGatewayException("AI service could not generate a valid response after $attemptNumber attempts. Please try again.")
    )
}
```

```kotlin
// A facade that needs to fail on a null AI payload keeps the 502 contract.
return openAIAPIClientService.makeRequest(/* ... */)
    .switchIfEmpty(Mono.error(BadGatewayException("AI returned no content")))
```

## Bad

```kotlin
return openAIAPIClientService.makeRequest(/* ... */)
    .onErrorResume { error ->
        // Hides an upstream AI outage behind a misleading 400 / silent empty success.
        Mono.error(BadRequestException("Could not generate content"))
    }
```
