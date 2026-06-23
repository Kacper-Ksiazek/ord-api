# Model closed sets as enums that carry their own data and helpers

For fixed sets of options (tones, types, avatars), use an `enum class` with constructor properties instead of loose strings. Attach related data (AI instructions, descriptions, ids) directly to each constant, and expose derived collections through the enum's `companion object`. This keeps behavior next to the value and lets the compiler enforce exhaustiveness.

## Good

```kotlin
@ExportToOpenAPI
enum class ConversationTone(
    val instructionForAI: String
) {
    FRIENDLY("Be conversational, warm, and approachable. ..."),
    FORMAL("Maintain professional boundaries and proper etiquette. ..."),
    NEUTRAL("Maintain a balanced, objective tone. ...")
}

// Companion helpers for derived collections
enum class ConversationAIBotAvatar(val id: String, val gender: Gender, val description: String) {
    AVATAR_DEFAULT("AVATAR_DEFAULT", Gender.MALE, "..."),
    AVATAR_ALPHA("AVATAR_ALPHA", Gender.FEMALE, "...");

    companion object {
        fun getSelectableAvatars(): List<ConversationAIBotAvatar> =
            entries.filter { it != AVATAR_DEFAULT }
    }
}
```

## Bad

```kotlin
// Stringly-typed: no compiler checks, instruction text duplicated everywhere it is used
val tone: String = "FRIENDLY"

fun instructionFor(tone: String): String = when (tone) {
    "FRIENDLY" -> "Be conversational, warm, and approachable. ..."
    else -> "" // silently wrong for any new/typo'd value
}
```
