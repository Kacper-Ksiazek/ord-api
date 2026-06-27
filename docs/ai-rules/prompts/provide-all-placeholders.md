# Provide every `{{placeholder}}` the template declares

`Prompt`'s `init` block validates that no `{{...}}` token remains after substitution and throws `Missing prompt params: ...` if any are left. Always pass a value for every placeholder in the template; use an explicit sentinel like `"NONE"` for optional/absent values rather than omitting the key.

## Good

```kotlin
val prompt = Prompt(
    variant = AvailablePrompts.CONVERSATION_GENERATE_AI_INTERLOCUTOR,
    params = mapOf(
        "language" to body.language.toString(),
        "level" to languageProficiency.level.toString(),
        "topic" to body.topic,
        "type" to body.conversationType.toString(),
        "typeExplanation" to body.conversationType.contextForAI,
        "additionalContext" to (body.additionalContext ?: "NONE"),
        "recentInterlocutors" to formattedRecentInterlocutors,
        "availableAvatars" to availableAvatars,
    )
)
```

## Bad

```kotlin
// Template also contains {{additionalContext}}, {{recentInterlocutors}},
// {{availableAvatars}} -> constructor throws "Missing prompt params: ...".
val prompt = Prompt(
    variant = AvailablePrompts.CONVERSATION_GENERATE_AI_INTERLOCUTOR,
    params = mapOf(
        "language" to body.language.toString(),
        "level" to languageProficiency.level.toString(),
        "topic" to body.topic,
        "type" to body.conversationType.toString(),
        "typeExplanation" to body.conversationType.contextForAI,
    )
)
```
