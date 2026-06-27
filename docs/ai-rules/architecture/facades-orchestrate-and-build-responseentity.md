# Facades orchestrate and build the ResponseEntity

Facades are the only layer that assembles `ResponseEntity` (status code + body) and orchestrates multiple services/mappers to fulfill a request. They convert entities to DTOs via injected mappers and choose the HTTP status (`201 CREATED` on create, `200 OK` on read, `204 NO_CONTENT` on delete). The facade interface returns `Mono<ResponseEntity<T>>`; its `@Service` impl lives in the `impl/` subpackage.

## Good

```kotlin
@Service
class ConversationCRUDFacadeImpl(
    private val conversationService: ConversationService,
    private val conversationMapper: ConversationMapper,
    private val languageProficiencyService: LanguageProficiencyService,
) : ConversationCRUDFacade {

    override fun createConversation(userId: UUID, body: CreateConversationRequest): Mono<ResponseEntity<ConversationDTO>> {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(userId, body.language)
            .flatMap { proficiency -> conversationService.save(ConversationEntity(/* ... */)) }
            .map { entity ->
                ResponseEntity.status(HttpStatus.CREATED).body(conversationMapper.toDTO(entity))
            }
    }
}
```

## Bad

```kotlin
// Facade returning a raw DTO and leaking ResponseEntity assembly to the controller
interface ConversationCRUDFacade {
    fun createConversation(userId: UUID, body: CreateConversationRequest): Mono<ConversationDTO>
}
```
