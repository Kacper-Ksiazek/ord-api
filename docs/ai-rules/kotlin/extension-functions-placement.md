# Put conversions and helpers in extension functions next to their type

Express type-specific conversions and utilities as top-level extension functions. Place domain-specific extensions in an `extensions/` package beside the model they extend (e.g. `models/conversation/extensions/`), and generic, reusable extensions in `shared/extensions/`. Make generic ones `<T>` parameterized where it applies.

## Good

```kotlin
// features/conversation/models/conversation/extensions/ConversationDTOExtensions.kt
fun ConversationDTO.convertToPromptParams(): Map<String, String> {
    return mapOf(
        "language" to language.toString(),
        "topic" to topic,
        "tone" to aiTone.toString(),
        "additionalContext" to (additionalContext ?: "-")
    )
}

// shared/extensions/ListExtensions.kt
fun <T> List<T>.convertToSetExplicitly(paramName: String? = null): Set<T> {
    if (this.size != this.toSet().size) {
        throw BadRequestException("Give parameter $paramName contains duplicates")
    }
    return this.toSet()
}
```

## Bad

```kotlin
// A static utility holder bolted onto an unrelated service, far from the type it operates on
@Component
class ConversationUtils {
    fun convertToPromptParams(dto: ConversationDTO): Map<String, String> { /* ... */ }
    fun <T> listToSet(list: List<T>): Set<T> { /* ... */ }
}
```
