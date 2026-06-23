# Block reactive chains in tests, never in main code

Test code may call `.block()` on `Mono`/`Flux` to set up data and read assertions synchronously — this is the standard pattern in test helpers, seeders, and `@AfterEach` cleanup. This is the opposite of production code, where blocking the reactive chain is forbidden. Keep `.block()` confined to `src/test`: use it to persist fixtures, fetch entities for assertions, and tear data down.

## Good

```kotlin
// Test-only: blocking to persist a fixture and to verify deletion.
private fun createConversationEntity(userId: UUID): ConversationEntity =
    conversationRepository.save(ConversationEntity(/* ... */, userId = userId)).block()!!

@Test
fun `204 - should delete conversation`() {
    val created = createConversationEntity(userId = authenticatedUser.userInfo.id)

    conversationAPIClient.deleteConversation(created.id!!, authenticatedUser)

    conversationRepository.findById(created.id).block() shouldBe null
}
```

## Bad

```kotlin
// Wrong place: .block() must never appear in production reactive code under src/main.
@GetMapping("/{id}")
fun getConversation(@PathVariable id: UUID): ConversationDTO {
    return conversationRepository.findById(id).block()!! // breaks the reactive pipeline
}
```
