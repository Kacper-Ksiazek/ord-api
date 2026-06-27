# Every prompt is a `.md` file registered in `AvailablePrompts`

Every AI prompt lives as a Markdown template under `src/main/resources/prompts/<feature>/` and is registered as an entry in the `AvailablePrompts` enum that maps it to its `resourcePath`. Never inline prompt text as a Kotlin string literal; the enum is the single source of truth for which prompts exist.

## Good

```kotlin
enum class AvailablePrompts(
    val resourcePath: String,
    val structuredOutput: StructuredOutputTemplate? = null,
) {
    CONVERSATION_SUGGEST_TOPIC(resourcePath = "conversation/suggest_conversation_topic.md"),
    CONVERSATION_REQUEST_AI_RESPONSE(resourcePath = "conversation/respond_in_conversation.md"),
    WORDS_EXPLAIN(resourcePath = "words/explain_word.md"),
}
```

## Bad

```kotlin
// Prompt text hardcoded in Kotlin, not registered, not a resource file.
val prompt = """
    You are a foreign language private tutor.
    Continue the conversation in $language at level $level...
""".trimIndent()

openAIStreamClientService.makeRequest(prompt = prompt, /* ... */)
```
