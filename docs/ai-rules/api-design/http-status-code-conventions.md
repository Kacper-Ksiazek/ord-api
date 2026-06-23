# Return conventional HTTP status codes and document them accurately

Build the response with the correct status in the facade: `POST` create → `201 CREATED`, `GET` → `200 OK`, `DELETE` → `204 NO_CONTENT`. The `@ApiResponse(responseCode = ...)` on the controller must match the status the facade actually returns, because the documented code is what the TypeScript client is generated against.

## Good

```kotlin
// Facade returns the real status
override fun createConversation(...): Mono<ResponseEntity<ConversationDTO>> =
    ...map { ResponseEntity.status(HttpStatus.CREATED).body(it.toDTO()) }

override fun deleteConversation(...): Mono<ResponseEntity<Unit>> =
    conversationService.deleteById(...)
        .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.NO_CONTENT).build<Unit>() })
```

```kotlin
// Controller documents the same codes
@PostMapping("/")
@ApiResponses(value = [ApiResponse(responseCode = "201", description = "Conversation created successfully"), ...])
fun createConversation(...) = ...

@DeleteMapping("/{conversationId}")
@ApiResponses(value = [ApiResponse(responseCode = "204", description = "Conversation deleted successfully"), ...])
fun deleteConversation(...) = ...
```

## Bad

```kotlin
// Facade returns 201 CREATED ...
.map { ResponseEntity.status(HttpStatus.CREATED).body(it.toDTO()) }

// ... but the doc claims 200, so the generated TS client expects the wrong code
@PostMapping("/")
@ApiResponses(value = [ApiResponse(responseCode = "200", description = "Conversation created successfully")])
fun createConversation(...) = ...
```
