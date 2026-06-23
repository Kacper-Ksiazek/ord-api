# Use named arguments when constructing models and calling multi-parameter functions

When building data classes or calling functions that take several parameters (especially same-typed ones like `UUID`/`String`), pass arguments by name. This keeps call sites self-documenting and prevents accidental argument transposition.

## Good

```kotlin
return ConversationDTO(
    id = entity.id ?: error("Conversation id must not be null"),
    topic = entity.topic,
    language = entity.language,
    proficiencyLevel = entity.proficiencyLevel,
    type = entity.type,
    aiTone = entity.aiTone,
    aiInterlocutorName = entity.aiInterlocutorName,
    aiInterlocutorAvatarId = entity.aiInterlocutorAvatarId,
    additionalContext = entity.additionalContext,
    messages = entity.messages.map { conversationMessageMapper.toDTO(it) }.toMutableList(),
    createdAt = entity.createdAt,
    updatedAt = entity.updatedAt
)
```

## Bad

```kotlin
// Positional args: unreadable and easy to swap topic/name or the two UUIDs
return ConversationDTO(
    entity.id!!,
    entity.topic,
    entity.language,
    entity.proficiencyLevel,
    entity.type,
    entity.aiTone,
    entity.aiInterlocutorName,
    entity.aiInterlocutorAvatarId
)
```
