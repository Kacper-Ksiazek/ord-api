# Build prompts with the `Prompt` class and `{{param}}` placeholders

Construct prompts only through `Prompt(variant, params)`. The template body is loaded from the classpath by `PromptCache` and every `{{placeholder}}` is substituted from `params`. Never read prompt files yourself or do manual string replacement.

## Good

```kotlin
val prompt = Prompt(
    variant = AvailablePrompts.CONVERSATION_SUGGEST_TOPIC,
    params = mapOf(
        "language" to body.language.toString(),
        "level" to languageProficiency.level.toString(),
        "type" to body.conversationType.toString(),
        "typeExplanation" to body.conversationType.contextForAI,
    )
)

openAIStreamClientService.makeRequest(prompt = prompt.toString(), /* ... */)
```

## Bad

```kotlin
// Bypassing Prompt/PromptCache and substituting placeholders by hand.
val template = this::class.java.classLoader
    .getResourceAsStream("prompts/conversation/suggest_conversation_topic.md")!!
    .bufferedReader().readText()

val prompt = template
    .replace("{{language}}", body.language.toString())
    .replace("{{level}}", level.toString())
```
