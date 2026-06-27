# Use TypeReference<T> for typed AI responses

For non-streaming structured responses, pass the expected type as `aiResponseType: TypeReference<T>` so Jackson can deserialize the model output (including generics like `List<...>`) correctly. Deserialize into a dedicated OpenAI-shaped DTO (e.g. `OpenAIGeneratedWordManual`) and convert to the domain with `toDomain(...)`, rather than parsing raw strings. For streamed arrays, pass the per-item type via `streamedItemType: TypeReference<TStreamedItem>`.

## Good

```kotlin
openAIAPIClientService
    .makeRequest(
        aiResponseType = object : TypeReference<OpenAIGeneratedWordManual>() {},
        prompt = prompt,
        userId = user.id,
        gptTokensUsageLogKey = GptTokensUsageOperationType.Words.GENERATE_MANUAL,
    )
    .map { it.toDomain(body.word) }
```

```kotlin
openAIAPIClientService.openStructuredArrayStream(
    prompt = prompt,
    streamedItemType = object : TypeReference<VocabularySuggestion>() {},
    userId = user.id,
    gptTokensUsageLogKey = GptTokensUsageOperationType.Words.SUGGEST_VOCABULARY,
)
```

## Bad

```kotlin
// Returning a raw String and re-parsing by hand defeats typed deserialization.
openAIAPIClientService
    .makeRequest(
        aiResponseType = object : TypeReference<String>() {},
        prompt = prompt,
        userId = user.id,
        gptTokensUsageLogKey = GptTokensUsageOperationType.Words.GENERATE_MANUAL,
    )
    .map { raw -> jacksonObjectMapper().readValue(raw, OpenAIGeneratedWordManual::class.java).toDomain(body.word) }
```
