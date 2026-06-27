# Never block the reactive chain

This is a fully reactive WebFlux backend. Never call `.block()`, `.blockFirst()`, `.blockLast()`, or `.toFuture().get()` in production code: it ties up an event-loop thread and defeats the reactive model. `.block()` is only acceptable in tests and in one-off bootstrap code (e.g. `Application.kt`). Compose with operators instead.

## Good

```kotlin
override fun createConversation(
    userId: UUID,
    body: CreateConversationRequest
): Mono<ResponseEntity<ConversationDTO>> {
    return languageProficiencyService
        .findUserProficiencyInLanguageOrThrow(userId, body.language)
        .flatMap { proficiency ->
            conversationService.save(
                ConversationEntity(/* ... */ userId = userId)
            )
        }
        .map { conversationEntity ->
            ResponseEntity.status(HttpStatus.CREATED).body(conversationEntity.toDTO())
        }
}
```

## Bad

```kotlin
override fun createConversation(
    userId: UUID,
    body: CreateConversationRequest
): ResponseEntity<ConversationDTO> {
    // Blocks an event-loop thread - forbidden in main code
    val proficiency = languageProficiencyService
        .findUserProficiencyInLanguageOrThrow(userId, body.language)
        .block()!!
    val saved = conversationService.save(ConversationEntity(/* ... */)).block()!!
    return ResponseEntity.status(HttpStatus.CREATED).body(saved.toDTO())
}
```
