# Use validateResponseBody / parseResponseBody to enforce correctness and drive retries

When a typed AI response can be malformed or semantically invalid, supply `validateResponseBody` (and, if you need to massage the payload, `parseResponseBody`). The client re-attempts the request up to `maximumNumberOfOpenAIAPIRequestAttempts` whenever the parsed body is `null` or `validateResponseBody` returns `false`. Put your "is this usable?" check here — e.g. try the `toDomain()` mapping — instead of letting bad data flow downstream. For graceful degradation after retries are exhausted, combine it with an `onErrorResume` fallback.

After retries are exhausted, `OpenAIAPIClientServiceImpl` surfaces `BadGatewayException` (502) — not `InternalServerError` (500). See also `surface-ai-failures-as-badgatewayexception.md`.

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

### Batch responses (item count must match the request)

When the prompt asks the model to return **one item per input** (e.g. QAW fill-gaps, game review), validate the array size inside `validateResponseBody` — not in a downstream `.map`. Also run `toDomain()` in validation so invalid enum values (e.g. bad `WordType`) trigger a retry instead of a 500.

Reference implementations: `QAWAIFacadeImpl.fillGaps`, `SentencesWritingAIReviewService.review`.

```kotlin
val expectedItemCount = body.items.size

openAIAPIClientService
    .makeRequest(
        aiResponseType = object : TypeReference<OpenAIQAWFillGapsBatch>() {},
        prompt = prompt,
        userId = user.id,
        gptTokensUsageLogKey = GptTokensUsageOperationType.QAW.FILL_GAPS,
        validateResponseBody = { batch ->
            if (batch == null || batch.items.size != expectedItemCount) {
                return@makeRequest false
            }
            try {
                batch.toDomain()
                true
            } catch (_: IllegalArgumentException) {
                false
            }
        },
    )
    .map { it.toDomain() }
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

```kotlin
// Throwing InternalServerError in .map for an AI shape mismatch skips client retries
// and surfaces 500 instead of 502 after exhaustion.
openAIAPIClientService
    .makeRequest(
        aiResponseType = object : TypeReference<OpenAIQAWFillGapsBatch>() {},
        prompt = prompt,
        userId = user.id,
        gptTokensUsageLogKey = GptTokensUsageOperationType.QAW.FILL_GAPS,
    )
    .map { batch ->
        if (batch.items.size != body.items.size) {
            throw InternalServerError(
                "AI returned ${batch.items.size} items but ${body.items.size} were requested.",
            )
        }
        batch.toDomain()
    }
```
