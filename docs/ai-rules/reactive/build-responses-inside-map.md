# Build HTTP responses inside map

Construct the `ResponseEntity` inside a `map` on the resolved value, so the response is built reactively once data is available. Use `map` for synchronous transformations of the emitted item and `flatMap` only when the transformation itself returns a `Mono`/`Flux`.

## Good

```kotlin
override fun getConversationById(
    userId: UUID,
    conversationId: UUID
): Mono<ResponseEntity<ConversationDTO>> {
    return conversationService
        .findByIdOrFailWithMessages(id = conversationId, userId = userId)
        .map { conversation ->
            ResponseEntity
                .status(HttpStatus.OK)
                .body(conversation)
        }
}
```

## Bad

```kotlin
override fun getConversationById(
    userId: UUID,
    conversationId: UUID
): Mono<ResponseEntity<ConversationDTO>> {
    // flatMap with Mono.just adds nothing but noise; map is the right operator
    return conversationService
        .findByIdOrFailWithMessages(id = conversationId, userId = userId)
        .flatMap { conversation ->
            Mono.just(ResponseEntity.status(HttpStatus.OK).body(conversation))
        }
}
```
