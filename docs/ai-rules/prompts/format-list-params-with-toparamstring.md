# Format list-valued prompt params with `toParamString`

When a placeholder needs a list of values, render it with the `List<String>.toParamString(...)` helper instead of ad-hoc string building. Use `tabulated = true` for a bulleted block (`- item` per line) and the default for an inline `[a, b, c]` form. This keeps list formatting consistent across prompts.

## Good

```kotlin
val prompt = Prompt(
    variant = AvailablePrompts.CONVERSATION_SUGGEST_TOPIC,
    params = mapOf(
        "examples" to body.conversationType.examplesForAI.toParamString(tabulated = true),
        "topicsToExclude" to allTopicsToExclude.toParamString(tabulated = true),
        // ...
    )
)
```

## Bad

```kotlin
val prompt = Prompt(
    variant = AvailablePrompts.CONVERSATION_SUGGEST_TOPIC,
    params = mapOf(
        // Re-implements list formatting inconsistently at each call site.
        "examples" to body.conversationType.examplesForAI.joinToString(" | "),
        "topicsToExclude" to allTopicsToExclude.toString(),
        // ...
    )
)
```
