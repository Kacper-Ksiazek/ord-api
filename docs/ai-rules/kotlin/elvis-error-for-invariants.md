# Unwrap "should never be null" invariants with `?: error(...)`, not `!!`

When a nullable value must be present for the code to be correct (e.g. a persisted entity's `id`), unwrap it with the Elvis operator and `error("...")` to fail loudly with a descriptive message. Avoid bare `!!` in main code; reserve `!!` for tests and tightly-scoped reactive `map { it!! }` after an explicit `switchIfEmpty` guarantees presence.

## Good

```kotlin
override fun toDTO(entity: ConversationEntity): ConversationDTO {
    return ConversationDTO(
        id = entity.id ?: error("Conversation id must not be null"),
        topic = entity.topic,
        // ...
    )
}
```

## Bad

```kotlin
override fun toDTO(entity: ConversationEntity): ConversationDTO {
    return ConversationDTO(
        id = entity.id!!, // throws an opaque NPE with no context about what was null or why
        topic = entity.topic,
        // ...
    )
}
```
