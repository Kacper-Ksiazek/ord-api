# Controllers only delegate

Controllers own HTTP concerns only: routing (`@RequestMapping`/`@GetMapping`/...), OpenAPI annotations, `@Valid` request binding, and extracting the authenticated user via `@AuthenticatedUser`. Each handler must immediately delegate to a facade and return its `Mono<ResponseEntity<T>>` (or `Flux<T>` for streaming) without any business logic, persistence, or mapping.

## Good

```kotlin
@PostMapping("/")
fun createConversation(
    @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    @Valid @RequestBody body: CreateConversationRequest
): Mono<ResponseEntity<ConversationDTO>> = conversationCRUDFacade.createConversation(
    userId = user.id,
    body = body
)
```

## Bad

```kotlin
@PostMapping("/")
fun createConversation(
    @AuthenticatedUser user: UserDTO,
    @RequestBody body: CreateConversationRequest
): Mono<ResponseEntity<ConversationDTO>> {
    // Business logic and ResponseEntity assembly do not belong in the controller
    val entity = ConversationEntity(topic = body.topic, userId = user.id, /* ... */)
    return conversationService.save(entity)
        .map { ResponseEntity.status(HttpStatus.CREATED).body(conversationMapper.toDTO(it)) }
}
```
