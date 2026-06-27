# Centralize fixtures in TestData objects, factories, and seeders

Do not scatter magic literals across tests. Put shared constants in a per-class `object TestData`, build persisted entities through a private helper with sensible defaults (overridable per test), and reuse the shared `seeders/`/`factories/` (e.g. `WordSeeder`, `WordFactory` built on `FactoryBase`'s `faker`) for cross-feature data. This keeps tests focused on the scenario under test and makes intent obvious.

## Good

```kotlin
object TestData {
    const val TOPIC = "Discussing the weather and climate change"
    val LANGUAGE = LanguageName.ENGLISH
    val TYPE = ConversationType.SMALL_TALK
    val TONE = ConversationTone.FRIENDLY
    const val AI_INTERLOCUTOR_NAME = "Dr. Smith"
}

private fun createConversationEntity(
    userId: UUID,
    topic: String = TestData.TOPIC,
    type: ConversationType = TestData.TYPE,
    updatedAt: Instant = Instant.now()
): ConversationEntity = conversationRepository.save(
    ConversationEntity(topic = topic, type = type, userId = userId, updatedAt = updatedAt, /* ... */)
).block()!!

// Reusable factory for unrelated features:
val words = wordSeeder.seedMultipleEntitiesForUser(userId = user.userInfo.id, amount = 5)
```

## Bad

```kotlin
// Duplicated literals and inline entity construction in every test.
@Test
fun test1() {
    conversationRepository.save(
        ConversationEntity(topic = "Discussing the weather and climate change",
            type = ConversationType.SMALL_TALK, userId = user.userInfo.id, /* ... */)
    ).block()
}
@Test
fun test2() {
    conversationRepository.save(
        ConversationEntity(topic = "Discussing the weather and climate change", /* copy-paste again */)
    ).block()
}
```
