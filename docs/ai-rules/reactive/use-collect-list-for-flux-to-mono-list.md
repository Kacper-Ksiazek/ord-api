# Use collectList() to turn Flux into Mono<List>

When an endpoint returns a finite collection (not a stream), map each `Flux` element, then `collectList()` to get a `Mono<List<T>>` you can wrap in a single `ResponseEntity`. Do not collect into an intermediate mutable list manually or block to drain the `Flux`.

## Good

```kotlin
override fun getManyConversations(
    userId: UUID,
    filters: ConversationListFilters,
): Mono<ResponseEntity<List<ConversationSummaryDTO>>> {
    return conversationService
        .findAllWithFilters(userId, filters)
        .map { it.toSummaryDTO() }
        .collectList()
        .map { conversationDTOs ->
            ResponseEntity
                .status(HttpStatus.OK)
                .body(conversationDTOs)
        }
}
```

## Bad

```kotlin
override fun getManyConversations(
    userId: UUID,
    filters: ConversationListFilters,
): Mono<ResponseEntity<List<ConversationSummaryDTO>>> {
    // toIterable() blocks the caller to drain the Flux
    val list = conversationService
        .findAllWithFilters(userId, filters)
        .map { it.toSummaryDTO() }
        .toIterable()
        .toList()
    return Mono.just(ResponseEntity.ok(list))
}
```
