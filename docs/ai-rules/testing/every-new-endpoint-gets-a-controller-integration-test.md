# Every new endpoint ships with a controller integration test

Every new HTTP endpoint must come with a controller integration test annotated with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `@AutoConfigureWebTestClient`. The test boots the real application against a Testcontainers Postgres and exercises the endpoint end-to-end (real routing, validation, persistence, AI calls). An endpoint without a test is considered incomplete.

## Good

```kotlin
@DisplayName("- ConversationController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000")
class TestConversationController @Autowired constructor(
    private val conversationRepository: ConversationRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    gptTokensUsageRepository: GptTokensUsageRepository
) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    gptTokensUsageRepository = gptTokensUsageRepository
) {
    private val conversationAPIClient = ConversationAPIClient(webClient)
    // @Nested Positive/Negative tests for each endpoint...
}
```

## Bad

```kotlin
// New POST /api/v1/conversations/ endpoint merged with no controller test.
// Only a unit test on the service exists, so routing, request validation,
// auth, and persistence are never exercised against a real running app.
@SpringBootTest
class ConversationServiceTest {
    @Test
    fun `creates conversation`() { /* mocks everything, no HTTP layer */ }
}
```
