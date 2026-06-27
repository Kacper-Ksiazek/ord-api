# Use centralized, stable operation keys

The `gptTokensUsageLogKey` must come from a constant in `GptTokensUsageOperationType` (e.g. `Conversation.AI_RESPONSE`, `Words.GENERATE_MANUAL`, or `Game.Generate.createKey(...)`). These string values are stable identifiers stored in the `gpt_tokens_usage` table and asserted in tests via `assertGptTokensLogCreated(userId, "OPERATION_KEY")`. Never inline a raw string or rename an existing key casually — changing the value breaks usage analytics and tests.

## Good

```kotlin
openAIAPIClientService.makeRequest(
    aiResponseType = object : TypeReference<OpenAIGeneratedAIInterlocutor>() {},
    prompt = prompt.toString(),
    userId = userId,
    gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.GENERATE_INTERLOCUTOR,
    structuredOutput = prompt.variant.structuredOutput,
)
```

```kotlin
// For dynamic game keys, use the provided factory so the value stays predictable.
val operationType = GptTokensUsageOperationType.Game.Generate.createKey(gameType.name)
```

## Bad

```kotlin
openAIAPIClientService.makeRequest(
    aiResponseType = object : TypeReference<OpenAIGeneratedAIInterlocutor>() {},
    prompt = prompt.toString(),
    userId = userId,
    gptTokensUsageLogKey = "generate-interlocutor-v2", // ad-hoc string, untracked, breaks assertGptTokensLogCreated
)
```
