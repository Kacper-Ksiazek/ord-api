# Wire structured-output prompts to a matching `StructuredOutputTemplate`

A prompt that expects a JSON response must declare a `StructuredOutputTemplate` schema and wire it into its `AvailablePrompts` entry via the `structuredOutput` parameter. The schema value is then passed to the AI client (`structuredOutput = prompt.variant.structuredOutput`). Plain free-text prompts leave `structuredOutput` unset.

## Good

```kotlin
// AvailablePrompts.kt
WORDS_GENERATE_MANUAL(
    resourcePath = "words/generate_word_manual.md",
    structuredOutput = generatedWordManualSchema,
),

// at the call site
openAIStreamClientService.makeRequest(
    prompt = prompt.toString(),
    aiResponseType = object : TypeReference<OpenAIGeneratedAIInterlocutor>() {},
    structuredOutput = prompt.variant.structuredOutput,
    // ...
)
```

## Bad

```kotlin
// JSON-producing prompt with no schema wired in -> output is unconstrained,
// and `prompt.variant.structuredOutput` is null at the call site.
WORDS_GENERATE_MANUAL(
    resourcePath = "words/generate_word_manual.md",
),
```
