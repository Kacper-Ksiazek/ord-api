# Bind enums with .name and read them with Enum.valueOf

PostgreSQL enum columns are exchanged as text over R2DBC. When binding a Kotlin enum into a query, always pass `enum.name` (never the enum instance). When reading an enum column back, cast the value to `String` and rebuild it with `EnumType.valueOf(row[...] as String)`.

## Good

```kotlin
// Binding
.bind("type", type.name)
.bind("language", language.name)

// Reading
language = LanguageName.valueOf(row["language"] as String),
type = ConversationType.valueOf(row["type"] as String),
aiTone = ConversationTone.valueOf(row["ai_tone"] as String),
val sender = ConversationMessageSender.valueOf(row["sender"] as String)
```

## Bad

```kotlin
// Binding the enum instance instead of its .name, and casting the
// column straight to the enum type without going through valueOf(String).
.bind("type", type)
.bind("language", language)

type = row["type"] as ConversationType,
val sender = row["sender"] as ConversationMessageSender
```
