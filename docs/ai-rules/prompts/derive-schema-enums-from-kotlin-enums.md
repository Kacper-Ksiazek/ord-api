# Derive schema `enum` values from Kotlin enums, never hardcode them

When a structured-output field accepts a fixed set of values, populate its `enum` from the backing Kotlin enum (`SomeEnum.entries`, or a dedicated helper like `ConversationAIBotAvatar.toSchemaEnumList()`). This keeps the JSON schema in sync with the domain model automatically. Never paste a literal list of strings.

## Good

```kotlin
"type" to mapOf(
    "type" to "string",
    "enum" to WordType.entries,
    "description" to "Type of word or expression"
),
"avatarId" to mapOf(
    "type" to "string",
    "enum" to ConversationAIBotAvatar.toSchemaEnumList(),
    "description" to "Avatar ID selected from the provided list"
)
```

## Bad

```kotlin
// Hardcoded values silently drift out of sync with the WordType enum.
"type" to mapOf(
    "type" to "string",
    "enum" to listOf("NOUN", "VERB", "ADJECTIVE"),
    "description" to "Type of word or expression"
)
```
