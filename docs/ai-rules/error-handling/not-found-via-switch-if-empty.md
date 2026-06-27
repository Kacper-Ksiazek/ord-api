# Translate empty results into NotFoundException with switchIfEmpty

When a lookup may return no element, convert the empty signal into a 404 using `switchIfEmpty(Mono.error(NotFoundException(...)))`. Provide a descriptive message that names the resource and identifier. This is the standard "find or fail" pattern across services.

## Good

```kotlin
import com.ord.exceptions.REST.NotFoundException

// ConversationServiceImpl
override fun findByIdOrFailWithMessages(id: UUID, userId: UUID): Mono<ConversationDTO> {
    return conversationRepository
        .findByIdOrFailWithMessages(id, userId)
        .switchIfEmpty(Mono.error(NotFoundException("Conversation with id $id not found")))
}
```

## Bad

```kotlin
// Silently returns an empty Mono -> caller can't tell "not found" from success,
// and no 404 is produced.
override fun findByIdOrFailWithMessages(id: UUID, userId: UUID): Mono<ConversationDTO> {
    return conversationRepository.findByIdOrFailWithMessages(id, userId)
}
```
