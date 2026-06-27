# Authenticate via mockAuthenticatedUser(...)

For authenticated requests, create the caller with `mockAuthenticatedUser(...)` from `ControllerTestBase`. It persists a `UserEntity`, mocks the OTP, performs the real `/api/v1/auth/otp-verify` flow, sets up `LanguageProficiencyEntity` rows, and returns a `MockedAuthenticatedUser` whose auth cookie the typed clients attach automatically. Pass `languages = mapOf(...)` to control the user's proficiencies, and use `mockAuthenticatedUserWithUninitializedAccount(...)` for onboarding cases. For unauthenticated negative tests pass `user = null`.

## Good

```kotlin
@Test
fun `200 - should generate AI interlocutor data with additional context`() {
    val authenticatedUser = mockAuthenticatedUser(
        languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
    )

    val response = conversationAPIClient.generateAIInterlocutor(
        body = request,
        user = authenticatedUser
    )

    response.status shouldBe HttpStatus.OK
}

@Test
fun `401 - anonymous user cannot generate AI interlocutor`() {
    val response = conversationAPIClient.generateAIInterlocutor(body = request, user = null)
    response.status shouldBe HttpStatus.UNAUTHORIZED
}
```

## Bad

```kotlin
// Hand-rolled user + token setup, bypassing the real auth flow.
val user = userRepository.save(UserEntity(email = "a@b.com", /* ... */)).block()!!
val fakeJwt = Jwts.builder().setSubject(user.id.toString()).compact()
webClient.get().uri("/api/v1/conversations/")
    .header("Authorization", "Bearer $fakeJwt") // not how the app authenticates
    .exchange()
```
