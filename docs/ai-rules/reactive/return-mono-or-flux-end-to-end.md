# Return Mono/Flux end-to-end

Keep the reactive type all the way from the controller through facades, services, and repositories. Controllers return `Mono<ResponseEntity<T>>` for single results and `Flux<T>` for streams; services and repositories return `Mono`/`Flux`. Never unwrap to a plain value in the middle of the stack.

## Good

```kotlin
// Controller
fun getConversationById(
    @AuthenticatedUser user: UserDTO,
    @PathVariable conversationId: UUID,
): Mono<ResponseEntity<ConversationDTO>> = conversationCRUDFacade.getConversationById(
    conversationId = conversationId,
    userId = user.id
)

// Service
override fun findByIdOrFailWithMessages(id: UUID, userId: UUID): Mono<ConversationDTO> {
    return conversationRepository
        .findByIdOrFailWithMessages(id, userId)
        .switchIfEmpty(Mono.error(NotFoundException("Conversation with id $id not found")))
}
```

## Bad

```kotlin
// Returning a materialized value forces a blocking boundary somewhere upstream
fun getConversationById(
    @AuthenticatedUser user: UserDTO,
    @PathVariable conversationId: UUID,
): ResponseEntity<ConversationDTO> {
    val dto = conversationCRUDFacade
        .getConversationById(conversationId, user.id)
        .block() // breaks the reactive chain
    return ResponseEntity.ok(dto)
}
```
