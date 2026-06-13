# Call endpoints through typed *APIClient wrappers

Tests must hit endpoints through a typed `*APIClient` (e.g. `ConversationAPIClient`) rather than using raw `WebTestClient` chains. Each client extends `APITestClient`, exposes one method per endpoint with strongly-typed request/response bodies, attaches the auth cookie via the `user` parameter, and returns an `APIClientResponse<T>` carrying `status`, `body`, `headers`, and `cookies`. When you add an endpoint, add a matching method to its `*APIClient` and call it from the test.

## Good

```kotlin
private val conversationAPIClient = ConversationAPIClient(webClient)

@Test
fun `200 - should return user's conversations`() {
    val authenticatedUser = mockAuthenticatedUser(
        languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
    )
    createConversationEntity(userId = authenticatedUser.userInfo.id)

    val response = conversationAPIClient.getConversations(user = authenticatedUser)

    response.status shouldBe HttpStatus.OK
    response.body!! shouldHaveSize 1
}
```

## Bad

```kotlin
// Raw WebTestClient in the test: duplicates URLs, auth cookie wiring,
// and body deserialization that the typed client already centralizes.
webClient.get()
    .uri("/api/v1/conversations/")
    .cookie(authenticatedUser.authCookie.name, authenticatedUser.authCookie.value)
    .exchange()
    .expectStatus().isOk
    .expectBodyList(ConversationSummaryDTO::class.java)
```
