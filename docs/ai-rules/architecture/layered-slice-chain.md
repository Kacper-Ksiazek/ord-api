# Respect the layered vertical-slice chain

Every feature is a self-contained vertical slice that flows strictly `Controller → Facade (interface + impl) → Service (interface + impl) → Repository (+ CustomMethods/impl) → Entity/Mapper/DTO`. A layer may only call the layer directly beneath it through that layer's interface; never skip layers (e.g. a controller calling a service or repository directly).

## Good

```kotlin
// Controller delegates to a Facade interface
class ConversationController(
    val conversationCRUDFacade: ConversationCRUDFacade,
) {
    fun createConversation(...) = conversationCRUDFacade.createConversation(userId = user.id, body = body)
}

// Facade impl calls the Service interface
@Service
class ConversationCRUDFacadeImpl(
    private val conversationService: ConversationService,
) : ConversationCRUDFacade {
    override fun createConversation(...) = conversationService.save(...)
}

// Service impl calls the Repository
@Service
class ConversationServiceImpl(
    private val conversationRepository: ConversationRepository
) : ConversationService { ... }
```

## Bad

```kotlin
// Controller reaching past the facade straight into the repository
@RestController
class ConversationController(
    val conversationRepository: ConversationRepository, // layer skipped
) {
    fun createConversation(...) = conversationRepository.save(...) // no facade, no service
}
```
