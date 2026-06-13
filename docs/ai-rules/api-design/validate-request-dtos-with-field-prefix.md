# Validate request DTOs with `@field:`-prefixed Jakarta constraints

Request bodies are immutable Kotlin `data class`es, and Jakarta validation annotations on their properties must use the `@field:` use-site target so the constraint lands on the backing field (where Hibernate Validator reads it). Controllers then enforce validation with `@Valid @RequestBody`.

## Good

```kotlin
data class CreateConversationRequest(
    @field:NotBlank(message = "Topic cannot be blank")
    @field:Size(min = 1, max = 500, message = "Topic must be between 1 and 500 characters")
    val topic: String,

    @field:Size(max = 5000, message = "Additional context must be less than 5000 characters")
    val additionalContext: String? = null,
)
```

```kotlin
fun createConversation(
    @Valid @RequestBody body: CreateConversationRequest
): Mono<ResponseEntity<ConversationDTO>> = ...
```

## Bad

```kotlin
data class CreateConversationRequest(
    @NotBlank                       // no @field: target — constraint may be ignored on the property
    @Size(min = 1, max = 500)
    val topic: String,
)

fun createConversation(
    @RequestBody body: CreateConversationRequest   // missing @Valid — DTO never validated
): Mono<ResponseEntity<ConversationDTO>> = ...
```
