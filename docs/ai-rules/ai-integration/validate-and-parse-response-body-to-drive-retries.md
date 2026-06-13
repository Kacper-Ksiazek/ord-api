# Use validateResponseBody / parseResponseBody to enforce correctness and drive retries

When a typed AI response can be malformed or semantically invalid, supply `validateResponseBody` (and, if you need to massage the payload, `parseResponseBody`). The client re-attempts the request up to `maximumNumberOfOpenAIAPIRequestAttempts` whenever the parsed body is `null` or `validateResponseBody` returns `false`. Put your "is this usable?" check here — e.g. try the `toDomain()` mapping — instead of letting bad data flow downstream. For graceful degradation after retries are exhausted, combine it with an `onErrorResume` fallback.

## Good

```kotlin
openAIAPIClientService
    .makeRequest(
        prompt = prompt.toString(),
        aiResponseType = object : TypeReference<OpenAIGeneratedAIInterlocutor>() {},
        userId = userId,
        gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.GENERATE_INTERLOCUTOR,
        structuredOutput = prompt.variant.structuredOutput,
        validateResponseBody = { openAIResponse ->
            try {
                openAIResponse?.toDomain() // invalid avatarId -> IllegalArgumentException -> retry
                true
            } catch (e: IllegalArgumentException) {
                logger.warn("Invalid AI interlocutor, retrying: {}", e.message)
                false
            }
        }
    )
    .map { it.toDomain() }
    .onErrorResume { Mono.just(GeneratedAIInterlocutorData(name = "AI Assistant", avatarId = ConversationAIBotAvatar.AVATAR_DEFAULT)) }
```

## Bad

```kotlin
openAIAPIClientService
    .makeRequest(
        prompt = prompt.toString(),
        aiResponseType = object : TypeReference<OpenAIGeneratedAIInterlocutor>() {},
        userId = userId,
        gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.GENERATE_INTERLOCUTOR,
        structuredOutput = prompt.variant.structuredOutput,
        // No validation: a bad avatarId throws later in the chain and surfaces as a 500.
    )
    .map { it.toDomain() }
```
