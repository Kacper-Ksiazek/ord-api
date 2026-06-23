# Sequence completion-only work with then() and Mono.fromCallable

For operations that emit nothing meaningful (e.g. `Mono<Void>` deletes), use `then(...)` to run the next step after completion, and wrap eager/synchronous response construction in `Mono.fromCallable { ... }` so it executes lazily on subscription rather than at assembly time.

## Good

```kotlin
override fun deleteConversation(
    userId: UUID,
    conversationId: UUID
): Mono<ResponseEntity<Unit>> {
    return conversationService.deleteById(
        id = conversationId,
        userId = userId,
    )
        .then(Mono.fromCallable {
            ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build<Unit>()
        })
}
```

## Bad

```kotlin
override fun deleteConversation(
    userId: UUID,
    conversationId: UUID
): Mono<ResponseEntity<Unit>> {
    // map on Mono<Void> never fires (no element emitted), so the response is lost;
    // and Mono.just builds the response eagerly at assembly time
    return conversationService.deleteById(id = conversationId, userId = userId)
        .map { ResponseEntity.status(HttpStatus.NO_CONTENT).build<Unit>() }
}
```
