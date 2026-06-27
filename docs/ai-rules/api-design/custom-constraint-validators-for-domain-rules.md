# Express domain validation as custom @Constraint annotations

Validation that goes beyond size/blank checks (verifying a value is a known avatar id, a safe string, a valid topic list) is implemented as a reusable Jakarta `@Constraint` annotation backed by a `ConstraintValidator`. Feature-specific rules live under `features/<feature>/validators/annotations/`; cross-cutting ones under `shared/api/annotations/validators/`. Apply them on DTO fields with the `@field:` prefix alongside the built-in constraints.

## Good

```kotlin
// features/conversation/validators/annotations/ValidConversationAIBotAvatarId.kt
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ConversationAIBotAvatarIdValidator::class])
annotation class ValidConversationAIBotAvatarId(
    val message: String = "Invalid avatar ID. Must be one of the valid ConversationAIBotAvatar values",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
```

```kotlin
data class CreateConversationRequest(
    @field:NotBlank(message = "AI interlocutor avatar ID is required")
    @field:ValidConversationAIBotAvatarId
    val aiInterlocutorAvatarId: String,
)
```

## Bad

```kotlin
// Re-implementing domain validation inline in the facade/service
fun createConversation(body: CreateConversationRequest) {
    if (body.aiInterlocutorAvatarId !in validAvatarIds) {   // not reusable, not in the OpenAPI/Bean-Validation pipeline
        throw IllegalArgumentException("bad avatar")
    }
}
```
