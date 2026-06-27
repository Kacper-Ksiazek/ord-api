# Assert AI token logging with assertGptTokensLogCreated

Any endpoint that calls the LLM must, in its positive tests, assert that token usage was recorded by calling `assertGptTokensLogCreated(userId, operationType)`. Token usage is persisted asynchronously, so the helper polls `GptTokensUsageRepository` with retries and then verifies that input/output tokens are greater than zero and the model matches the default. Pass the authenticated user's id and the exact `operationType` string the feature logs (e.g. `"CONVERSATION_GENERATE_INTERLOCUTOR"`, `"CONVERSATION_SUGGEST_TOPICS"`).

## Good

```kotlin
@Test
fun `200 - should successfully suggest topics with user clue`() {
    val user = mockAuthenticatedUser(
        languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.B2)
    )

    val response = conversationAPIClient.suggestTopics(body = request, user = user)

    response.status shouldBe HttpStatus.OK
    response.body.shouldNotBeNull()

    assertGptTokensLogCreated(user.userInfo.id, "CONVERSATION_SUGGEST_TOPICS")
}
```

## Bad

```kotlin
// AI endpoint test that only checks the HTTP response and ignores token accounting.
val response = conversationAPIClient.suggestTopics(body = request, user = user)
response.status shouldBe HttpStatus.OK
// no assertGptTokensLogCreated(...) -> a regression that stops logging usage goes unnoticed
```
