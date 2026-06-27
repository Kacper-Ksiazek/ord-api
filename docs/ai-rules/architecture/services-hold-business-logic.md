# Services hold business logic and expose `val repository`

Services contain domain/business logic and are HTTP-agnostic: they return `Mono<T>`/`Flux<T>` of entities or DTOs, never `ResponseEntity`. A service interface extends `UserResourceService<TEntity>`, and its `@Service` impl satisfies the base contract by overriding `val repository` with the concrete repository. Translate "not found" and similar domain conditions into exceptions here (e.g. `NotFoundException`), not into HTTP codes.

## Good

```kotlin
interface ConversationService : UserResourceService<ConversationEntity> {
    fun findByIdOrFailWithMessages(id: UUID, userId: UUID): Mono<ConversationDTO>
}

@Service
class ConversationServiceImpl(
    private val conversationRepository: ConversationRepository
) : ConversationService {
    override val repository: ConversationRepository = conversationRepository

    override fun findByIdOrFailWithMessages(id: UUID, userId: UUID): Mono<ConversationDTO> {
        return conversationRepository
            .findByIdOrFailWithMessages(id, userId)
            .switchIfEmpty(Mono.error(NotFoundException("Conversation with id $id not found")))
    }
}
```

## Bad

```kotlin
@Service
class ConversationServiceImpl(
    private val conversationRepository: ConversationRepository
) : ConversationService {
    // Missing `override val repository`, so inherited save/findById/deleteById break

    // Service must not know about HTTP status codes / ResponseEntity
    fun getById(id: UUID, userId: UUID): Mono<ResponseEntity<ConversationDTO>> { ... }
}
```
