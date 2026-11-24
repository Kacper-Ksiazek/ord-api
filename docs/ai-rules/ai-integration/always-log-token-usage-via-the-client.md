# Always log token usage by passing a gptTokensUsageLogKey

Every AI call MUST record token usage. The client does this for you: `OpenAIAPIClientServiceImpl` calls `gptTokensUsageService.saveTokensUsage(...)` with the `userId` and `gptTokensUsageLogKey` you pass, reading `input_tokens`/`output_tokens` from the OpenAI response. So always pass a real `userId` and a `gptTokensUsageLogKey` to `makeRequest`/`openSimpleStringStream`/`openStructuredArrayStream`. Do not call `gptTokensUsageService.saveTokensUsage(...)` yourself around an AI call — that would double-count.

The single exception is `makeRequest(..., trackUsage = false)`, reserved for marginal-cost, high-frequency calls where logging every request is noise (today only the explainer autocomplete, `AIExplainer.AUTOCOMPLETE`). Such operations still pass a real `gptTokensUsageLogKey` (it identifies the operation and its stub fixture) and must keep the prompt and output tiny. Never use it to hide a regular feature's cost.

## Good

```kotlin
openAIAPIClientService.openSimpleStringStream(
    prompt = prompt,
    userId = user.id,
    gptTokensUsageLogKey = GptTokensUsageOperationType.AIExplainer.EXPLAIN_PHRASE,
    onComplete = { (payload, emitter) -> emitter.tryEmitComplete() }
)
// Token usage is saved automatically on the RESPONSE_COMPLETED event.
```

## Bad

```kotlin
val response = openAIAPIClientService.makeRequest(
    aiResponseType = object : TypeReference<OpenAIGeneratedWordManual>() {},
    prompt = prompt,
    userId = user.id,
    gptTokensUsageLogKey = GptTokensUsageOperationType.Words.GENERATE_MANUAL,
)

// Double counting: the client already logged this call.
return response.doOnNext {
    gptTokensUsageService.saveTokensUsage(user.id, "WORDS_GENERATE_MANUAL", 0, 0).subscribe()
}
```

```kotlin
// Allowed exception: tab completion fires on every typing pause and costs a few tokens.
openAIAPIClientService.makeRequest(
    aiResponseType = object : TypeReference<OpenAIExplainPhraseAutocomplete>() {},
    prompt = prompt,
    userId = user.id,
    gptTokensUsageLogKey = AiProviderUsageOperationType.AIExplainer.AUTOCOMPLETE,
    trackUsage = false,
)
```

