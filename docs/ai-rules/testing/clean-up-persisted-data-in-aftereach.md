# Clean up persisted data in @AfterEach

Tests share one Testcontainers Postgres instance across the whole suite, so every test class must delete the rows it creates in an `@AfterEach`. Otherwise leftover entities leak between tests and cause flaky size/ordering assertions. Clean up the feature repositories your tests write to (e.g. `conversationRepository.deleteAll().block()`); base infrastructure data is managed separately.

## Good

```kotlin
@AfterEach
fun cleanup() {
    conversationRepository.deleteAll().block()
}
```

## Bad

```kotlin
// No cleanup: conversations created here survive into the next test,
// so `shouldHaveSize 1` and "empty list" assertions become order-dependent and flaky.
@Test
fun `200 - should return user's conversations`() {
    createConversationEntity(userId = authenticatedUser.userInfo.id)
    // ... and nothing ever deletes it
}
```
