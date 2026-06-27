# Handle missing reactive results with `switchIfEmpty(Mono.error(...))`

In WebFlux flows, an empty `Mono`/`Flux` represents "not found". Convert absence into a domain exception with `switchIfEmpty(Mono.error(...))` using the project's REST exceptions (`NotFoundException`, `BadRequestException`, etc.) and an interpolated, descriptive message. Do not let empty publishers silently propagate.

## Good

```kotlin
override fun findByIdOrFail(id: UUID, userId: UUID): Mono<ConversationEntity> {
    return conversationRepository
        .findByIdOrFailWithMessages(id, userId)
        .switchIfEmpty(Mono.error(NotFoundException("Conversation with id $id not found")))
}
```

## Bad

```kotlin
override fun findByIdOrFail(id: UUID, userId: UUID): Mono<ConversationEntity> {
    // Empty Mono leaks downstream; callers get a confusing empty response instead of a 404
    return conversationRepository.findByIdOrFailWithMessages(id, userId)
}
```
